package talentum.escenic.plugins.authenticator.agreements;


/**
 * The "lockable" agreement partner.
 * 
 * Articles can force authorization if lock field is checked.
 * Never authorize section.
 * 
 * @author stefan.norman
 */
public class LockableAgreement extends DefaultAgreement {

	public LockableAgreement() {
		super();
	}

	/**
	 * Check article lock field.
	 * 
	 * @param article
	 *            the article to check
	 * @return boolean false if article is locked
	 */
	public boolean allowArticle(Object article) {
		return !checkArticleField(article, "lock_article");
	}

	/**
	 * Check if a section should skip authorization 
	 * 
	 * @return boolean true if no further authorization should be done
	 */
	public boolean allowSection() {
		return true;
	}

}