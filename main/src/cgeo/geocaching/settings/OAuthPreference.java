package cgeo.geocaching.settings;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.R;
import cgeo.geocaching.connector.oc.OCDEAuthorizationActivity;
import cgeo.geocaching.connector.oc.OCPLAuthorizationActivity;
import cgeo.geocaching.twitter.TwitterAuthorizationActivity;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class OAuthPreference extends AbstractClickablePreference {

    private static final int NO_KEY = -1;

    private enum OAuthActivityMapping {
        NONE(NO_KEY, null),
        OCDE(R.string.pref_fakekey_ocde_authorization, OCDEAuthorizationActivity.class),
        OCPL(R.string.pref_fakekey_ocpl_authorization, OCPLAuthorizationActivity.class),
        TWITTER(R.string.pref_fakekey_twitter_authorization, TwitterAuthorizationActivity.class);

        public final int prefKeyId;
        public final Class<?> authActivity;

        OAuthActivityMapping(int prefKeyId, Class<?> clazz) {
            this.prefKeyId = prefKeyId;
            this.authActivity = clazz;
        }
    }

    private final OAuthActivityMapping oAuthMapping;

    private OAuthActivityMapping getAuthorization() {
        final String prefKey = getKey();
        for (OAuthActivityMapping auth : OAuthActivityMapping.values()) {
            if (auth.prefKeyId != NO_KEY && prefKey.equals(CgeoApplication.getInstance().getString(auth.prefKeyId))) {
                return auth;
            }
        }
        return OAuthActivityMapping.NONE;
    }

    public OAuthPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.oAuthMapping = getAuthorization();
    }

    public OAuthPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.oAuthMapping = getAuthorization();
    }

    @Override
    protected OnPreferenceClickListener getOnPreferenceClickListener(final SettingsActivity activity) {
        activity.setOcAuthTitle(oAuthMapping.prefKeyId);
        return new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (oAuthMapping.authActivity != null) {
                    Intent authIntent = new Intent(preference.getContext(),
                            oAuthMapping.authActivity);
                    activity.startActivityForResult(authIntent,
                            oAuthMapping.prefKeyId);
                }
                return false; // no shared preference has to be changed
            }
        };

    }
}
