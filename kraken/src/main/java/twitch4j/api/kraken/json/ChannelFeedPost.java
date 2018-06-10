package twitch4j.api.kraken.json;

import java.util.Calendar;
import lombok.Data;

/**
 * Model representing a Channel Feed Post.
 *
 * @author Philipp Heuer [https://github.com/PhilippHeuer]
 * @version %I%, %G%
 * @see User
 * @see ReactionList
 * @since 1.0
 */
@Data
public class ChannelFeedPost {

	/**
	 * Body Text
	 */
	private String body;

	/**
	 * Created At
	 */
	private Calendar createdAt;

	/**
	 * Was deleted?
	 */
	private Boolean deleted;

	/**
	 * Post Id
	 */
	private String id;

	/**
	 * Reactions
	 */
	private ReactionList reactions;

	/**
	 * Posting User
	 */
	private User user;

}