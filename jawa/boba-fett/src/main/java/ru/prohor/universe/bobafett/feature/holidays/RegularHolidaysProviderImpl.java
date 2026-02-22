package ru.prohor.universe.bobafett.feature.holidays;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Service
public class RegularHolidaysProviderImpl implements RegularHolidaysProvider {
    private static final TypeReference<List<List<HolidaysDay>>> TYPE_REFERENCE = new TypeReference<>() {};
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path holidaysDir;

    private int lastYear = LocalDate.now(DateTimeUtil.MOSCOW_ZONE_ID).getYear();
    private HolidaysYear current;
    private HolidaysYear next;

    public RegularHolidaysProviderImpl(
            @Value("${universe.boba-fett.holidays-path}") String holidaysDir
    ) {
        this.holidaysDir = Path.of(holidaysDir);
        updateYear();
    }

    @Override
    public List<String> getForDate(int currentYear, LocalDate date) {
        checkYear(currentYear);
        HolidaysYear sourceYear;
        if (date.getYear() == lastYear)
            sourceYear = current;
        else if (date.getYear() == lastYear + 1)
            sourceYear = next;
        else
            throw new IllegalArgumentException("The holiday date year must be the current or next year");
        return getFromSource(sourceYear, date);
    }

    private synchronized List<String> getFromSource(HolidaysYear sourceYear, LocalDate date) {
        return sourceYear
                .month(date.getMonthValue() - 1)
                .day(date.getDayOfMonth() - 1)
                .holidays();
    }

    private synchronized void checkYear(int currentYear) {
        if (currentYear == lastYear) {
            return;
        }
        int diff = currentYear - lastYear;
        if (diff < 0) {
            throw new IllegalArgumentException(
                    "Year cannot change in reverse order, last year = " + lastYear + ", current = " + currentYear
            );
        }
        this.lastYear = currentYear;
        updateYear();
    }

    private void updateYear() {
        try {
            this.current = loadHolidaysYear(lastYear);
            this.next = loadHolidaysYear(lastYear + 1);
        } catch (IOException e) {
            throw new RuntimeException("Error loading holidays year", e);
        }
    }

    private HolidaysYear loadHolidaysYear(int year) throws IOException {
        List<List<HolidaysDay>> months = objectMapper.readValue(
                Files.readString(holidaysDir.resolve(year + ".json")),
                TYPE_REFERENCE
        );
        return new HolidaysYear(months.stream().map(HolidaysMonth::new).toList());
    }

    private record HolidaysYear(List<HolidaysMonth> months) {
        public HolidaysMonth month(int index) {
            return months.get(index);
        }
    }

    private record HolidaysMonth(List<HolidaysDay> days) {
        public HolidaysDay day(int index) {
            return days.get(index);
        }
    }

    private record HolidaysDay(List<String> holidays, int day) {}
}
