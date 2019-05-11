package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import judgels.gabriel.api.CompilationException;
import judgels.gabriel.api.EvaluationException;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.languages.cpp.CppGradingLanguage;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.GradingSource;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
import judgels.gabriel.sandboxes.fake.FakeSandboxFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BlackBoxGradingEngineTest {

    protected static final Verdict VERDICT_CE = new Verdict("CE", "Compilation Error");
    protected static final Verdict VERDICT_OK = new Verdict("OK", "OK");
    protected static final Verdict VERDICT_AC = new Verdict("AC", "Accepted");
    protected static final Verdict VERDICT_WA = new Verdict("WA", "Wrong Answer");
    protected static final Verdict VERDICT_TLE = new Verdict("TLE", "Time Limit Exceeded");
    protected static final Verdict VERDICT_RTE = new Verdict("RTE", "Runtime Error");

    private GradingLanguage language;

    private File workerDir;

    private File sourceDir;
    private File graderDir;
    private File sandboxDir;

    private Map<String, File> sourceFiles;

    private final Map<String, File> testDataFiles;
    private final Map<String, File> helperFiles;

    protected BlackBoxGradingEngineTest(String resourceDirname) {
        File resourceDir = new File(BlackBoxGradingEngineTest.class.getClassLoader().getResource("blackbox/" + resourceDirname).getPath());

        this.sourceDir = new File(resourceDir, "source");

        File testDataDir = new File(resourceDir, "testdata");
        File helperDir = new File(resourceDir, "helper");

        this.testDataFiles = listFilesAsMap(testDataDir);
        this.helperFiles = listFilesAsMap(helperDir);

        this.language = new CppGradingLanguage();
    }

    @BeforeEach
    public void setUp() {
        workerDir = Files.createTempDir();

        graderDir = new File(workerDir, "grader");
        sandboxDir = new File(workerDir, "sandbox");

        try {
            FileUtils.forceMkdir(graderDir);
            FileUtils.forceMkdir(sandboxDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create temp directories");
        }

        sourceFiles = Maps.newHashMap();
    }

    @AfterEach
    public void tearDown() {
        try {
            FileUtils.forceDelete(workerDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete temp directories");
        }
    }

    protected final void addSourceFile(String key, String filename) {
        sourceFiles.put(key, new File(sourceDir, filename));
    }

    protected final GradingResult runEngine(BlackBoxGradingEngine grader, GradingConfig config) throws GradingException {
        SandboxFactory sandboxFactory = new FakeSandboxFactory(sandboxDir);
        try {
            return grader.grade(graderDir, config, language, new GradingSource(sourceFiles, testDataFiles, helperFiles), sandboxFactory);
        } catch (PreparationException | CompilationException | EvaluationException | ScoringException e) {
            throw new GradingException(e);
        }
    }

    private Map<String, File> listFilesAsMap(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return ImmutableMap.of();
        }
        return Arrays.asList(files).stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
    }
}
