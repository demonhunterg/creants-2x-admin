package com.creants.v2.admin.handlers.events;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.QAntEventParam;
import com.creants.creants_2x.core.controllers.system.Login;
import com.creants.creants_2x.core.exception.QAntException;
import com.creants.creants_2x.core.extension.BaseServerEventHandler;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;

/**
 * @author LamHM
 *
 */
public class LoginEvtHandler extends BaseServerEventHandler {
	private static final String LDAP_URL = "ldap://ldap.creants.net:389";
	private static final String SECURITY_PRINCIPAL_FORMAT = "mail=%s@creants.net,ou=users,domainName=creants.net,o=domains,dc=creants,dc=net";
	private static final String SEARCH_FORMAT = "(mail=%s@creants.net)";


	@Override
	public void handleServerEvent(IQAntEvent event) throws QAntException {
		String name = (String) event.getParameter(QAntEventParam.LOGIN_NAME);
		String pass = (String) event.getParameter(QAntEventParam.LOGIN_PASSWORD);
		QAntTracer.info(this.getClass(), "[INFO] admin login:" + name);
		try {
			IQAntObject paramsOut = (IQAntObject) event.getParameter(QAntEventParam.LOGIN_OUT_DATA);
			login(name, pass, paramsOut);
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}


	private void login(String username, String password, IQAntObject paramsOut) throws NamingException {
		Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, LDAP_URL);

		env.put(Context.SECURITY_PRINCIPAL, String.format(SECURITY_PRINCIPAL_FORMAT, username));
		env.put(Context.SECURITY_CREDENTIALS, password);
		InitialDirContext ctx = new InitialDirContext(env);

		SearchControls searchControls = new SearchControls();
		String[] attributeFilter = { "cn", "mail", "givenname", "employeenumber", "sn", "title", "mobile" };
		searchControls.setReturningAttributes(attributeFilter);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> enumeration = ctx.search("dc=creants,dc=net",
				String.format(SEARCH_FORMAT, username), searchControls);
		SearchResult searchResult = (SearchResult) enumeration.next();
		Attributes attribs = searchResult.getAttributes();
		paramsOut.putUtfString(Login.NEW_LOGIN_NAME, "admin#" + username);
		paramsOut.putUtfString("mail", (String) attribs.get("mail").get());
		paramsOut.putUtfString("fn", (String) attribs.get("givenname").get() + " " + (String) attribs.get("sn").get());
		paramsOut.putUtfString("title", (String) attribs.get("title").get());
		paramsOut.putUtfString("mobile", (String) attribs.get("mobile").get());
		paramsOut.putUtfString("cn", (String) attribs.get("cn").get());
		ctx.close();
	}

}
