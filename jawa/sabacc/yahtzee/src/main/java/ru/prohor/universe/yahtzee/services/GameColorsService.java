package ru.prohor.universe.yahtzee.services;

import org.springframework.stereotype.Service;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

import java.util.List;

@Service
public class GameColorsService {
    public AccountController.AvailableColorsResponse getAvailableColors() {
        return new AccountController.AvailableColorsResponse(List.of()); // TODO
    }
}
