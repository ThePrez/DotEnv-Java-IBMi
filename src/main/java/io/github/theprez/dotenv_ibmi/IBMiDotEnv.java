package io.github.theprez.dotenv_ibmi;

import java.beans.PropertyVetoException;
import java.io.IOException;

import com.github.theprez.jcmdutils.StringUtils;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;

import io.github.cdimascio.dotenv.Dotenv;

public class IBMiDotEnv {
    private static Dotenv s_dotEnv = null;
    private static boolean s_isIBMi = checkIsIBMi();
    private volatile static AS400 s_conn = null;

    private static boolean checkIsIBMi() {
        final String osName = System.getProperty("os.name", "Unknown");
        return "os400".equalsIgnoreCase(osName) || "os/400".equalsIgnoreCase(osName);
    }

    public static synchronized Dotenv getDotEnv() {
        if (null != s_dotEnv) {
            return s_dotEnv;
        }
        final String cwd = System.getProperty("user.dir", ".");
        final Dotenv dotenv = Dotenv.configure().directory(cwd).ignoreIfMalformed().ignoreIfMissing().load();
        return s_dotEnv = dotenv;
    }

    public static synchronized AS400 getCachedSystemConnection(final boolean _starCurrentIfPossible) throws IOException, AS400SecurityException {
        if (null != s_conn) {
            return s_conn;
        }
        return s_conn = getNewSystemConnection(_starCurrentIfPossible);
    }

    public static AS400 getNewSystemConnection(final boolean _starCurrentIfPossible)
            throws IOException, AS400SecurityException {
        final Dotenv dotenv = getDotEnv();
        final boolean isStarCurrentDefault = isIBMi() && _starCurrentIfPossible;
        final String hostname = dotenv.get("IBMI_HOSTNAME", isIBMi() ? "localhost" : null);
        final String username = dotenv.get("IBMI_USERNAME", isStarCurrentDefault ? "*CURRENT" : null);
        final String pw = dotenv.get("IBMI_PASSWORD", isStarCurrentDefault ? "*CURRENT" : null);

        final AS400 ret = new AS400(hostname);
        try {
            ret.setUserId(username);
            if (StringUtils.isNonEmpty(pw)) {
                ret.setPassword(pw.toCharArray());
            }

            if (isIBMi()) {
                ret.setGuiAvailable(false);
            }
        } catch (final PropertyVetoException e) {
            throw new IOException(e);
        }
        return ret;

    }

    public static boolean isIBMi() {
        return s_isIBMi;
    }
}
