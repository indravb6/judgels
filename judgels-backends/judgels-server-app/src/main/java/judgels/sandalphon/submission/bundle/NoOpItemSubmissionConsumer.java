package judgels.sandalphon.submission.bundle;

import java.util.Map;
import java.util.Optional;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;

public class NoOpItemSubmissionConsumer implements ItemSubmissionConsumer {
    @Override
    public void accept(ItemSubmission submission, Map<String, Optional<Grading>> itemGradingsMap) {}
}
