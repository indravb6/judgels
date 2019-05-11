package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.batch.BatchWithSubtasksGradingConfig;
import judgels.gabriel.languages.cpp.CppGradingLanguage;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public final class BatchWithSubtasksGradingEngineTest extends BlackBoxGradingEngineTest {

    private final BatchWithSubtasksGradingEngine engine;
    private final BatchWithSubtasksGradingConfig config;

    private final int timeLimit;
    private final int memoryLimit;
    private final List<TestGroup> testData;
    private final List<Integer> subtaskPoints;

    public BatchWithSubtasksGradingEngineTest() {
        super("batch");

        this.timeLimit = 1000;
        this.memoryLimit = 65536;

        this.testData = ImmutableList.of(
                TestGroup.of(0, ImmutableList.of(
                        TestCase.of("sample_1.in", "sample_1.out", ImmutableSet.of(0, 1, 2)),
                        TestCase.of("sample_2.in", "sample_2.out", ImmutableSet.of(0, 1, 2)),
                        TestCase.of("sample_3.in", "sample_3.out", ImmutableSet.of(0, 2))
                )),
                TestGroup.of(1, ImmutableList.of(
                        TestCase.of("1_1.in", "1_1.out", ImmutableSet.of(1, 2)),
                        TestCase.of("1_2.in", "1_2.out", ImmutableSet.of(1, 2))
                )),

                TestGroup.of(2, ImmutableList.of(
                        TestCase.of("2_1.in", "2_1.out", ImmutableSet.of(2)),
                        TestCase.of("2_2.in", "2_2.out", ImmutableSet.of(2)),
                        TestCase.of("2_3.in", "2_3.out", ImmutableSet.of(2))
                ))
        );

        this.subtaskPoints = ImmutableList.of(30, 70);

        this.config = new BatchWithSubtasksGradingConfig.Builder()
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testData(testData)
                .subtaskPoints(subtaskPoints)
                .build();
        this.engine = new BatchWithSubtasksGradingEngine();
        this.engine.setScorerLanguage(new CppGradingLanguage());
    }

    @Test
    public void testCE() {
        addSourceFile("source", "aplusb-CE.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_CE);
            assertEquals(result.getScore(), 0);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertTrue(details.getCompilationOutputs().get("source").contains("BB"));
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testAC() {
        addSourceFile("source", "aplusb-AC.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_AC);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                    new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                    new SubtaskFinalResult(2, VERDICT_AC, 70.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30() {
        addSourceFile("source", "aplusb-WA-at-2_3.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_WA, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30BecauseTLE() {
        addSourceFile("source", "aplusb-TLE-at-2_3.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_TLE);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_TLE, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30BecauseWAAtSample() {
        addSourceFile("source", "aplusb-WA-at-sample_3.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_WA, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }


    @Test
    public void testOK0() {
        addSourceFile("source", "aplusb-WA-at-1_1.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 0);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_WA, 0.0),
                            new SubtaskFinalResult(2, VERDICT_WA, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOKWithMinimumScore() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-2_3.cpp");
        GradingResult result = runEngine(engine, createConfigWithCustomScorer("scorer-nonbinary-OK10.cpp"));
        assertEquals(result.getVerdict(), VERDICT_OK);
        assertEquals(result.getScore(), 40);

        BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
        assertEquals(details.getSubtaskResults(), ImmutableList.of(
                new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                new SubtaskFinalResult(2, VERDICT_OK, 10.0))
        );
    }

    @Test
    public void testACWithCustomScorer() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCustomScorer("scorer-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_AC);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_AC, 70.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30WithCustomScorer() {
        addSourceFile("source", "aplusb-WA-at-2_3-scorer.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCustomScorer("scorer-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_WA, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testInternalErrorBecauseCustomScorerCE() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");

        try {
            runEngine(engine, createConfigWithCustomScorer("scorer-CE.cpp"));
            fail();
        } catch (GradingException e) {
            assertTrue(e.getCause() instanceof PreparationException);
            assertTrue(e.getMessage().contains("fabs"));
        }
    }

    @Test
    public void testInternalErrorBecauseCustomScorerRTE() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");

        try {
            runEngine(engine, createConfigWithCustomScorer("scorer-RTE.cpp"));
            fail();
        } catch (GradingException e) {
            assertTrue(e.getCause() instanceof ScoringException);
        }
    }

    @Test
    public void testInternalErrorBecauseCustomScorerOutputUnknownFormat() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");

        try {
            runEngine(engine, createConfigWithCustomScorer("scorer-WA.cpp"));
            fail();
        } catch (GradingException e) {
            assertTrue(e.getCause() instanceof ScoringException);
            assertTrue(e.getMessage().contains("Unknown verdict"));
        }
    }

    private BatchWithSubtasksGradingConfig createConfigWithCustomScorer(String customScorer) {
        return new BatchWithSubtasksGradingConfig.Builder().from(config).customScorer(customScorer).build();
    }
}
