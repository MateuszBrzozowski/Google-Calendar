package pl.mbrzozowski.google.calendar;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Setter
@ToString
@EqualsAndHashCode
public class EventLink implements Supplier<String> {
    private String name;
    private String description;
    private LocalDateTime startTime;
    private int period = 0;
    @Setter(AccessLevel.NONE)
    private PeriodType periodType = PeriodType.MINUTES;
    public static final String ROOT_URL = "https://calendar.google.com/calendar/u/0/r/eventedit";

    public EventLink(String name) {
        setName(name);
    }

    /**
     * Set name of event
     *
     * @param name of event
     * @throws IllegalArgumentException if name is null or blank
     */
    public void setName(String name) {
        validName(name);
        this.name = name;
    }

    /**
     * Set period of event
     *
     * @param period time in minute
     * @throws IllegalArgumentException if period is non-positive
     */
    public final void setPeriod(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Non-positive period.");
        }
        this.period = period;
    }

    public final void setPeriod(PeriodType type, int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Non-positive period.");
        }
        this.period = period;
        this.periodType = type;
    }

    private void validName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name of event is not valid");
        }
    }

    private String addDescription(String link) {
        if (StringUtils.isBlank(description)) {
            return link;
        }
        link += "&details=" + URLEncoder.encode(description, StandardCharsets.UTF_8);
        return link;
    }

    private @NotNull String addEventName(String link) {
        link += "?text=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
        return link;
    }

    private String addDateTime(String link) {
        if (startTime == null) {
            return link;
        }
        String dateTimeAsString = getDateAsString(startTime);
        String endTimeAsString;
        if (period <= 0) {
            endTimeAsString = getDateAsString(startTime.plusHours(1));
        } else {
            switch (periodType) {
                case MINUTES -> endTimeAsString = getDateAsString(startTime.plusMinutes(period));
                case HOURS -> endTimeAsString = getDateAsString(startTime.plusHours(period));
                default -> throw new UnsupportedOperationException("Unsupported period type");
            }
        }
        return link + ("&dates=" + dateTimeAsString + "/" + endTimeAsString);
    }

    @NotNull
    private String getDateAsString(@NotNull LocalDateTime dateTime) {
        String dateTimeAsString = dateTime.toString();
        dateTimeAsString = dateTimeAsString.replaceAll("-", "").replaceAll(":", "");
        int index = dateTimeAsString.indexOf(".");
        dateTimeAsString = dateTimeAsString.substring(0, index);
        return dateTimeAsString;
    }

    /**
     * @return Link as string to create event in google calendar
     */
    @Override
    public final String get() {
        String link = ROOT_URL;
        link = addEventName(link);
        link = addDescription(link);
        link = addDateTime(link);
        return link;
    }

    public enum PeriodType {
        MINUTES,
        HOURS
    }
}
