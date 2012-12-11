package talentum.escenic.plugins.authenticator.agreements;


/**
 * The "section" agreement partner.
 * 
 * Never authorize articles.
 * Always authorize section.
 * 
 * @author stefan.norman
 */
public class SectionAgreement extends DefaultAgreement {

	public SectionAgreement() {
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
		return true;
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