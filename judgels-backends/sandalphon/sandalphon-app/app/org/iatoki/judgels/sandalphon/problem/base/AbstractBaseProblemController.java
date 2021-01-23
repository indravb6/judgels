package org.iatoki.judgels.sandalphon.problem.base;

import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.jophiel.JophielSessionUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.AbstractSandalphonController;
import org.iatoki.judgels.sandalphon.problem.base.version.html.versionLocalChangesWarningLayout;
import play.mvc.Result;

public abstract class AbstractBaseProblemController extends AbstractSandalphonController {
    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation("Problems", routes.ProblemController.index());
        return super.renderTemplate(template);
    }

    protected void appendVersionLocalChangesWarning(HtmlTemplate template, ProblemService problemService, Problem problem) {
        String userJid = JophielSessionUtils.getUserJid(template.getRequest());
        if (problemService.userCloneExists(userJid, problem.getJid())) {
            template.setWarning(versionLocalChangesWarningLayout.render(problem.getId(), null));
        }
    }
}
