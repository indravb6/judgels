package judgels.michael.problem.base;

import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.ProblemRoleChecker;
import judgels.sandalphon.problem.base.ProblemStore;

public abstract class BaseProblemResource extends BaseResource {
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemRoleChecker problemRoleChecker;

    protected String resolveStatementLanguage(HttpServletRequest req, Actor actor, Problem problem, Set<String> enabledLanguages) {
        String language = (String) req.getSession().getAttribute("statementLanguage");
        if (language == null || !enabledLanguages.contains(language)) {
            language = problemStore.getStatementDefaultLanguage(actor.getUserJid(), problem.getJid());
        }

        setCurrentStatementLanguage(req, language);
        return language;
    }

    protected String resolveEditorialLanguage(HttpServletRequest req, Actor actor, Problem problem, Set<String> enabledLanguages) {
        String language = (String) req.getSession().getAttribute("statementLanguage");
        if (language == null || !enabledLanguages.contains(language)) {
            language = problemStore.getEditorialDefaultLanguage(actor.getUserJid(), problem.getJid());
        }

        setCurrentStatementLanguage(req, language);
        return language;
    }

    protected HtmlTemplate newProblemsTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setActiveSidebarMenu("problems");
        return template;
    }

    protected HtmlTemplate newProblemTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = this.newProblemsTemplate(actor);
        template.setTitle("#" + problem.getId() + ": " + problem.getSlug());
        template.addMainTab("general", "General", "/problems/" + problem.getId());
        template.addMainTab("statements", "Statements", "/problems/" + problem.getType().name().toLowerCase() + "/" + problem.getId() + "/statements");
        template.addMainTab("editorials", "Editorials", "/problems/" + problem.getId() + "/editorials");
        return template;
    }

    protected HtmlTemplate newProblemStatementTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("statements");
        template.addSecondaryTab("view", "View", "/problems/" + problem.getType().name().toLowerCase() + "/" + problem.getId() + "/statements");
        template.addSecondaryTab("edit", "Edit", "/problems/" + problem.getId() + "/statements/edit");
        template.addSecondaryTab("media", "Media", "/problems/" + problem.getId() + "/statements/media");
        template.addSecondaryTab("languages", "Languages", "/problems/" + problem.getId() + "/statements/languages");
        return template;
    }

    protected HtmlTemplate newProblemEditorialTemplate(Actor actor, Problem problem, boolean hasEditorial) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("editorials");
        if (hasEditorial) {
            template.addSecondaryTab("view", "View", "/problems/" + problem.getId() + "/editorials");
            template.addSecondaryTab("edit", "Edit", "/problems/" + problem.getId() + "/editorials/edit");
            template.addSecondaryTab("media", "Media", "/problems/" + problem.getId() + "/editorials/media");
            template.addSecondaryTab("languages", "Languages", "/problems/" + problem.getId() + "/editorials/languages");
        }
        return template;
    }

    protected HtmlTemplate newProblemEditorialTemplate(Actor actor, Problem problem) {
        return newProblemEditorialTemplate(actor, problem, true);
    }
}
