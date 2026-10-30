package ru.prohor.universe.kenobi.plugin.english.model

import org.bson.types.ObjectId

/**
 * A user's vocabulary and persisted training state, shared across devices.
 *
 * Issuing a training batch does not update word review history. Review history is
 * updated only after the user completes the batch and its results are saved.
 *
 * @property id The owner's ID, matching `AuthorizedUser.objectId`.
 * @property words The user's full vocabulary with its original translations.
 * @property reviews Review history keyed by [Word.englishForm]. A missing entry
 * means the word has not yet been recorded in a completed training batch.
 * @property training The currently issued batch, reused until completion while
 * all its words remain in the vocabulary. Stores word forms rather than copies
 * of their translations; cleared when the batch is completed.
 * @property lastCompletedTrainingId The most recently saved batch ID. Retrying
 * its completion does not update review history again. This is not a full
 * history of completed batch IDs.
 */
data class WordsSet(
    val id: ObjectId,
    val words: List<Word>,
    val reviews: Map<String, WordReview> = emptyMap(),
    val training: TrainingBatch? = null,
    val lastCompletedTrainingId: String? = null,
)

data class Word(
    val englishForm: String,
    val translates: List<String>
)

/**
 * Persisted spaced-repetition progress for one English word or phrase.
 *
 * Progress applies to the entire English form, including all its meanings.
 * If the word required repetition at least once within the batch, it returns
 * to stage 1 with a review scheduled in one day.
 * Successful early practice preserves both the stage and the scheduled date.
 *
 * @property stage The current repetition stage, from 1 to 5; distinct from the
 * number of completed training batches.
 * @property nextReviewAt The scheduled review time, in milliseconds since the Unix epoch.
 * @property lastReviewedAt The most recent completed training time, in milliseconds
 * since the Unix epoch, including early practice and difficult reviews.
 * @property trainingCount The number of completed batches containing this word.
 * The first completion sets it to 1; each subsequent completion increments it
 * once, regardless of how many times the card appeared within that batch.
 */
data class WordReview(
    val stage: Int,
    val nextReviewAt: Long,
    val lastReviewedAt: Long,
    val trainingCount: Int,
)

data class TrainingBatch(
    val id: String,
    val englishForms: List<String>,
)
