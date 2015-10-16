package io.rapidpro.flows.runner;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.rapidpro.flows.utils.FlowUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A URN for a contact (e.g. a telephone number or twitter handle)
 */
public class ContactUrn {

    public enum Scheme {
        TEL,
        TWITTER
    }

    protected static String ANON_MASK = "********";

    protected Scheme m_scheme;

    protected String m_path;

    public ContactUrn(Scheme scheme, String path) {
        m_scheme = scheme;
        m_path = path;
    }

    /**
     * Parses a URN from a string
     * @param urn the string, e.g. tel:+260964153686, twitter:joe
     * @return the parsed URN
     */
    public static ContactUrn fromString(String urn) {
        String[] parts = urn.split(":", 2);
        Scheme scheme = Scheme.valueOf(parts[0].toUpperCase());
        return new ContactUrn(scheme, parts[1]);
    }

    /**
     * Returns a normalized version of this URN
     * @param org the org
     * @return the normalized URN
     */
    public ContactUrn normalized(Org org) {
        String normPath;
        if (m_scheme == ContactUrn.Scheme.TWITTER) {
            normPath = m_path.trim();
            if (normPath.charAt(0) == '@') {
                normPath = normPath.substring(1);
            }
        } else {
            normPath = FlowUtils.normalizeNumber(m_path, org.getCountry()).getLeft();
        }

        return new ContactUrn(m_scheme, normPath);
    }

    /**
     * Gets a representation of the URN for display
     */
    public String getDisplay(Org org, boolean full) {
        if (org.isAnon()) {
            return ANON_MASK;
        }

        if (m_scheme == Scheme.TEL && !full) {
            // if we don't want a full tell, see if we can show the national format instead
            try {
                if (StringUtils.isNotEmpty(m_path) && m_path.charAt(0) == '+') {
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    return phoneUtil.format(phoneUtil.parse(m_path, null), PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                }
            }
            catch (Exception ignored) {}
        }

        return m_path;
    }

    public Scheme getScheme() {
        return m_scheme;
    }

    public String getPath() {
        return m_path;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return m_scheme.name().toLowerCase() + ":" + m_path;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactUrn that = (ContactUrn) o;

        if (m_scheme != that.m_scheme) return false;
        return m_path.equals(that.m_path);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = m_scheme.hashCode();
        result = 31 * result + m_path.hashCode();
        return result;
    }
}
