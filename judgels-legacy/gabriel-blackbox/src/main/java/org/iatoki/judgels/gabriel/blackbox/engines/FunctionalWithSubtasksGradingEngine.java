package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import judgels.gabriel.aggregators.MinAggregator;
import judgels.gabriel.api.TestCaseAggregator;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.functional.FunctionalWithSubtasksGradingConfig;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
import org.iatoki.judgels.gabriel.blackbox.Compiler;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.CustomScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.DiffScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.FunctionalCompiler;
import org.iatoki.judgels.gabriel.blackbox.algorithms.FunctionalEvaluator;
import org.iatoki.judgels.gabriel.blackbox.languages.Cpp11GradingLanguage;
import org.iatoki.judgels.gabriel.sandboxes.Sandbox;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class FunctionalWithSubtasksGradingEngine extends BlackBoxGradingEngine {

    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private TestCaseAggregator aggregator;

    private int scoringTimeLimit;
    private int scoringMemoryLimit;

    private GradingLanguage gradingLanguage;
    private GradingLanguage scorerLanguage;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    public FunctionalWithSubtasksGradingEngine() {
        this.scoringTimeLimit = 10000;
        this.scoringMemoryLimit = 1024 * 1024;

        this.gradingLanguage = new Cpp11GradingLanguage();
        this.scorerLanguage = new Cpp11GradingLanguage();
    }

    @Override
    public String getName() {
        return "Functional with Subtasks";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new FunctionalWithSubtasksGradingConfig.Builder()
                .timeLimit(getDefaultCompilationTimeLimitInMilliseconds())
                .memoryLimit(getDefaultMemoryLimitInKilobytes())
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) throws IOException {
        return MAPPER.readValue(json, FunctionalWithSubtasksGradingConfig.class);
    }

    @Override
    protected void prepareAlgorithms(GradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        FunctionalWithSubtasksGradingConfig castConfig = (FunctionalWithSubtasksGradingConfig) config;

        compilerSandbox = sandboxFactory.newSandbox();
        compiler = new FunctionalCompiler(compilerSandbox, getCompilationDir(), gradingLanguage, sourceFiles, helperFiles, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes());
        evaluatorSandbox = sandboxFactory.newSandbox();
        evaluator = new FunctionalEvaluator(evaluatorSandbox, getCompilationDir(), getEvaluationDir(), castConfig.getTimeLimit(), castConfig.getMemoryLimit());

        if (castConfig.getCustomScorer().isPresent()) {
            scorerSandbox = sandboxFactory.newSandbox();
            File scorerFile = helperFiles.get(castConfig.getCustomScorer().get());
            scorer = new CustomScorer(scorerSandbox, getScoringDir(), scorerLanguage, scorerFile, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes(), scoringTimeLimit, scoringMemoryLimit);
        } else {
            scorer = new DiffScorer();
        }

        aggregator = new MinAggregator();
    }

    void setGradingLanguage(GradingLanguage gradingLanguage) {
        this.gradingLanguage = gradingLanguage;
    }

    void setScorerLanguage(GradingLanguage scorerLanguage) {
        this.scorerLanguage = scorerLanguage;
    }

    @Override
    protected TestCaseAggregator getAggregator() {
        return aggregator;
    }

    @Override
    protected Scorer getScorer() {
        return scorer;
    }

    @Override
    protected Compiler getCompiler() {
        return compiler;
    }

    @Override
    protected Evaluator getEvaluator() {
        return evaluator;
    }

    @Override
    protected void cleanUp() {
        if (compilerSandbox != null) {
            compilerSandbox.cleanUp();
        }
        if (evaluatorSandbox != null) {
            evaluatorSandbox.cleanUp();
        }
        if (scorerSandbox != null) {
            scorerSandbox.cleanUp();
        }
    }
}
