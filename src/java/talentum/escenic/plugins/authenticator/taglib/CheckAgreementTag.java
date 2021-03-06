package talentum.escenic.plugins.authenticator.taglib;

import javax.servlet.jsp.JspException;

import neo.taglib.article.AbstractArticleTag;
import neo.xredsys.api.AgreementInfo;
import neo.xredsys.content.AgreementManager;
import neo.xredsys.presentation.PresentationArticle;
import talentum.escenic.plugins.authenticator.agreements.DefaultAgreement;

public class CheckAgreementTag extends AbstractArticleTag {
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {

		PresentationArticle presentationArticle = getDefaultPresentationArticle();
		if(presentationArticle == null) {
			throw new JspException("No article found.");
		}

		// article is readable by default
		boolean result = true;
		
		// only check articles with "locked" home sections
		if (presentationArticle.getHomeSection().isAgreementRequired()) {
			
			// get the agreement info. I e "basic", "article", "lockable", etc
			AgreementInfo agreementInfo = presentationArticle.getHomeSection()
					.getAgreementInfo();
			if (agreementInfo == null) {
				BROWSER.error("Agreement info is not set in an agreement required section: "
						+ presentationArticle.getHomeSection().getName());
			} else {
				AgreementManager manager = AgreementManager
						.getAgreementManager();
				// get the agreement partner that matches the agreement info
				DefaultAgreement agreement = (DefaultAgreement) manager
						.getAgreementPartnerFor(agreementInfo
								.getAgreementInfo());
				if (agreement == null) {
					BROWSER.error("Agreement "
							+ agreementInfo.getAgreementInfo() + " not found");
				} else {
					result = agreement.allowArticle(presentationArticle);
				}
			}

		}

		pageContext.setAttribute(getId(), new Boolean(result));

		return super.doStartTag();
	}

}
