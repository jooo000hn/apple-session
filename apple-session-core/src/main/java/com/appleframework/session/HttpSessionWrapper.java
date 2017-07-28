package com.appleframework.session;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.appleframework.session.data.SessionCache;
import com.appleframework.session.data.SessionMap;

@SuppressWarnings("deprecation")
public class HttpSessionWrapper implements HttpSession {
	
	private final ServletContext servletContext;
	private SessionMap sessionMap;
	private SessionCache sessionCache;
	private boolean old;
	
	public HttpSessionWrapper(SessionMap sessionMap, SessionCache sessionCache, ServletContext servletContext) {
		this.sessionMap = sessionMap;
		this.sessionCache = sessionCache;
		this.servletContext = servletContext;
	}
	
	public void setMaxInactiveInterval(int interval) {
		sessionMap.setMaxInactiveInterval(interval);
		sessionCache.setMaxInactiveInterval(sessionMap.getId(), interval);
	}
	
	public void setAttribute(String name, Object value) {
		sessionMap.setAttribute(name, value);
		sessionCache.put(sessionMap.getId(), sessionMap, sessionMap.getMaxInactiveInterval());
	}
	
	public void removeAttribute(String name) {
		sessionMap.removeAttribute(name);
		sessionCache.put(sessionMap.getId(), sessionMap, sessionMap.getMaxInactiveInterval());
	}

	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	public void removeValue(String name) {
		removeAttribute(name);
	}

	public long getCreationTime() {
		return sessionMap.getCreationTime();
	}

	public String getId() {
		return sessionMap.getId();
	}

	public long getLastAccessedTime() {
		return sessionMap.getLastAccessedTime();
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public int getMaxInactiveInterval() {
		return sessionMap.getMaxInactiveInterval();
	}


	public Object getAttribute(String name) {
		return sessionMap.getAttribute(name);
	}

	public Object getValue(String name) {
		return getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(sessionMap.getAttributeNames());
	}

	public String[] getValueNames() {
		Set<String> attrs = sessionMap.getAttributeNames();
		return attrs.toArray(new String[0]);
	}

	public void invalidate() {
		sessionMap.setInvalidated(true);
		sessionCache.destroy(sessionMap.getId());
		setCurrentSession(null);
	}

	public void setNew(boolean isNew) {
		this.old = !isNew;
	}

	public boolean isNew() {
		return !old;
	}

	public boolean isInvalidated() {
		return sessionMap.isInvalidated();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}
	
	private void setCurrentSession(HttpSessionWrapper currentSession) {
		if(currentSession == null) {
			removeAttribute(SessionHttpServletRequestWrapper.CURRENT_SESSION_ATTR);
		} else {
			setAttribute(SessionHttpServletRequestWrapper.CURRENT_SESSION_ATTR, currentSession);
		}
	}
	

}
