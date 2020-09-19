package us.com.plattrk.api.model;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class ActiveDirectory {

    private static final Logger LOG = Logger.getLogger(ActiveDirectory.class.getName());

    private Properties properties;
    private DirContext dirContext;
    private SearchControls searchCtls;
    private String[] returnAttributes = { "member", "memberof" };
    private String baseFilter = "(&(objectCategory=group)(name=PLATTRK_ADM))";

    public ActiveDirectory(String username, String password, String domainUrl, String contextFactory) {
        properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        properties.put(Context.PROVIDER_URL, domainUrl);
        properties.put(Context.SECURITY_PRINCIPAL, username);
        properties.put(Context.SECURITY_CREDENTIALS, password);
        try {
            dirContext = new InitialDirContext(properties);
        } catch (NamingException e) {
            LOG.severe("ActiveDirectory::ActiveDirectory " + e.getMessage());
        }
        searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setReturningAttributes(returnAttributes);
    }

    public NamingEnumeration<SearchResult> searchUser(String searchBase) throws NamingException {
        return this.dirContext.search(searchBase, this.baseFilter, this.searchCtls);
    }

    public void closeLdapConnection() {
        try {
            if (dirContext != null)
                dirContext.close();
        } catch (NamingException e) {
            LOG.severe("ActiveDirectory::closeLdapConnection " + e.getMessage());
        }
    }
}
