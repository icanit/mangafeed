package eu.kanade.tachiyomi.data.database.models;

import android.content.Context;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.io.Serializable;

import eu.kanade.tachiyomi.R;
import eu.kanade.tachiyomi.data.database.tables.MangaTable;
import eu.kanade.tachiyomi.util.UrlUtil;

@StorIOSQLiteType(table = MangaTable.TABLE)
public class Manga implements Serializable {

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_ID, key = true)
    public Long id;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_SOURCE)
    public int source;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_URL)
    public String url;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_ARTIST)
    public String artist;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_AUTHOR)
    public String author;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_DESCRIPTION)
    public String description;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_GENRE)
    public String genre;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_TITLE)
    public String title;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_STATUS)
    public int status;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_THUMBNAIL_URL)
    public String thumbnail_url;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_FAVORITE)
    public boolean favorite;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_LAST_UPDATE)
    public long last_update;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_INITIALIZED)
    public boolean initialized;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_VIEWER)
    public int viewer;

    @StorIOSQLiteColumn(name = MangaTable.COLUMN_CHAPTER_FLAGS)
    public int chapter_flags;

    public int unread;

    public int category;

    public static final int UNKNOWN = 0;
    public static final int ONGOING = 1;
    public static final int COMPLETED = 2;
    public static final int LICENSED = 3;

    public static final int SORT_AZ   = 0x00000000;
    public static final int SORT_ZA   = 0x00000001;
    public static final int SORT_MASK = 0x00000001;

    public static final int SHOW_UNREAD = 0x00000002;
    public static final int SHOW_READ   = 0x00000004;
    public static final int READ_MASK   = 0x00000006;

    public static final int SHOW_DOWNLOADED     = 0x00000008;
    public static final int SHOW_NOT_DOWNLOADED = 0x00000010;
    public static final int DOWNLOADED_MASK     = 0x00000018;

    // Generic filter that does not filter anything
    public static final int SHOW_ALL    = 0x00000000;

    public static final int DISPLAY_NAME   = 0x00000000;
    public static final int DISPLAY_NUMBER = 0x00100000;
    public static final int DISPLAY_MASK   = 0x00100000;

    public Manga() {}

    public static Manga create(String pathUrl) {
        Manga m = new Manga();
        m.url = pathUrl;
        return m;
    }

    public void setUrl(String url) {
        this.url = UrlUtil.getPath(url);
    }

    public void copyFrom(Manga other) {
        if (other.title != null)
            title = other.title;

        if (other.author != null)
            author = other.author;

        if (other.artist != null)
            artist = other.artist;

        if (other.url != null)
            url = other.url;

        if (other.description != null)
            description = other.description;

        if (other.genre != null)
            genre = other.genre;

        if (other.thumbnail_url != null)
            thumbnail_url = other.thumbnail_url;

        status = other.status;

        initialized = true;
    }

    public String getStatus(Context context) {
        switch (status) {
            case ONGOING:
                return context.getString(R.string.ongoing);
            case COMPLETED:
                return context.getString(R.string.completed);
            case LICENSED:
                return context.getString(R.string.licensed);
            default:
                return context.getString(R.string.unknown);
        }
    }

    public void setChapterOrder(int order) {
        setFlags(order, SORT_MASK);
    }

    public void setDisplayMode(int mode) {
        setFlags(mode, DISPLAY_MASK);
    }

    public void setReadFilter(int filter) {
        setFlags(filter, READ_MASK);
    }

    public void setDownloadedFilter(int filter) {
        setFlags(filter, DOWNLOADED_MASK);
    }

    private void setFlags(int flag, int mask) {
        chapter_flags = (chapter_flags & ~mask) | (flag & mask);
    }

    public boolean sortChaptersAZ() {
        return (chapter_flags & SORT_MASK) == SORT_AZ;
    }

    // Used to display the chapter's title one way or another
    public int getDisplayMode() {
        return chapter_flags & DISPLAY_MASK;
    }

    public int getReadFilter() {
        return chapter_flags & READ_MASK;
    }

    public int getDownloadedFilter() {
        return chapter_flags & DOWNLOADED_MASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Manga manga = (Manga) o;

        return url.equals(manga.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
