package io.sensable.client.settings;

/**
 * Holds configuration variables. Using a Java class is more efficient than
 * using a property file since it doesn't have to
 * load and parse a .properties file from disk.
 * <p/>
 * Also it prevents all the headaches that come with
 * having to read files from disk in separate Threads.
 *
 * @author mmarcon
 */
public class Config {

    /**
     * Filename for <em>on disk</em> SQLite database.
     * Rule repository, item cache and possibly other
     * structured data will be stored in this file.
     */
    public static final String SENSABLE_STORAGE_DB = "sensable_storage.db";

    /**
     * Filename for <em>on disk</em> dump of the
     * SQLite database.
     * <p/>
     * Used when a debug method writes a copy of the database
     * somewhere where it can be retrieved via ADT.
     */
    public static final String SENSABLE_STORAGE_DB_DUMP = "sensable_storage__dump.db";

    /**
     * DB version for SQLite database.
     */
    public static final int SENSABLE_STORAGE_DB_VERSION = 6;

}

