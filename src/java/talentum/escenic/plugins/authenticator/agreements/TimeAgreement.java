package talentum.escenic.plugins.authenticator.agreements;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The "time" agreement partner.
 * 
 * Articles can skip authorization if publishdate matches or override field is checked.
 * Always authorize section.
 * 
 * @author stefan.norman
 */
public class TimeAgreement extends DefaultAgreement {

	private static Log log = LogFactory.getLog(TimeAgreement.class);

	int allowPublishedBeforeDays = 0;

	int allowPublishedBeforeWeekday = 0;

	String allowPublishedBeforeTime = "23:59";

	public TimeAgreement() {
		super();
	}

	public int getAllowPublishedBeforeDays() {
		return allowPublishedBeforeDays;
	}

	public void setAllowPublishedBeforeDays(int allowPublishedBeforeDays) {
		this.allowPublishedBeforeDays = allowPublishedBeforeDays;
	}

	public int getAllowPublishedBeforeWeekday() {
		return allowPublishedBeforeWeekday;
	}

	public void setAllowPublishedBeforeWeekday(int allowPublishedBeforeWeekday) {
		this.allowPublishedBeforeWeekday = allowPublishedBeforeWeekday;
	}

	public String getAllowPublishedBeforeTime() {
		return allowPublishedBeforeTime;
	}

	public void setAllowPublishedBeforeTime(String allowPublishedBeforeTime) {
		this.allowPublishedBeforeTime = allowPublishedBeforeTime;
	}


	/**
	 * Check an article and its publishing date is before last publishing
	 * weekday. Also check field override.
	 * 
	 * @param article
	 *            Object Using reflection to get by problem in ECE 5 where the
	 *            PresentationArticle class is not available in the shared class
	 *            loader
	 * @return boolean true if checks for publish date and override field pass
	 */
	public boolean allowArticle(Object article) {
		if (article != null) {

			if (getAllowPublishedBeforeDays() > 0) {

				Calendar cal = Calendar.getInstance();
				// first set time
				StringTokenizer tokenizer = new StringTokenizer(
						getAllowPublishedBeforeTime(), ":");
				cal.set(Calendar.HOUR_OF_DAY,
						Integer.parseInt(tokenizer.nextToken()));
				cal.set(Calendar.MINUTE,
						Integer.parseInt(tokenizer.nextToken()));
				// roll calendar back preferred days
				cal.add(Calendar.DATE, (0 - getAllowPublishedBeforeDays()));
				// if configured, roll calendar back to the closest matching
				// weekday
				if (getAllowPublishedBeforeWeekday() > 0) {
					while (cal.get(Calendar.DAY_OF_WEEK) != getAllowPublishedBeforeWeekday()) {
						cal.add(Calendar.DATE, -1);
					}
				}

				Date publishDate = null;
				try {
					publishDate = (Date) article.getClass()
							.getMethod("getPublishedDateAsDate", null)
							.invoke(article, null);
				} catch (Exception e) {
					log.error("Method invocation failed", e);
				}
				if (log.isDebugEnabled()) {
					log.debug("article publishing date: " + publishDate);
					log.debug("configured edition publishing date: "
							+ cal.getTime());
				}
				if (publishDate == null || publishDate.before(cal.getTime())) {
					return true;
				}
			}
			// Check "override_agreement" field. Users can allow an article to
			// pass agreement by checking a field in the article.
			if (checkArticleField(article, "override_agreement")) {
				return true;
			}
		}
		return false;
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