package talentum.escenic.plugins.authenticator.agreements;


/**
 * The "basic" agreement partner.
 * 
 * Articles can skip authorization if override field is checked.
 * Always authorize section.
 * 
 * @author stefan.norman
 */
public class BasicAgreement extends DefaultAgreement {

	public BasicAgreement() {
		super();
	}

	/**
	 * Check article field override.
	 * 
	 * @param article
	 *            the article to check
	 * @return boolean true if check for override field pass
	 */
	public boolean allowArticle(Object article) {
		return checkArticleField(article, "override_agreement");
	}

	/**
	 * Check if a section should skip authorization 
	 * 
	 * @return boolean true if no further authorization should be done
	 */
	public boolean allowSection() {
		return false;
	}

}