/*
 * freenet - IdentityPage.java
 * Copyright © 2008 David Roden
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package plugins.WoT.ui.web;

import java.util.Iterator;

import plugins.WoT.Identity;
import plugins.WoT.Trust;
import plugins.WoT.exceptions.UnknownIdentityException;
import freenet.support.HTMLNode;
import freenet.support.api.HTTPRequest;

/**
 * @author xor (xor@freenetproject.org)
 * @author bombe (bombe@freenetproject.org)
 * @version $Id$
 */
public class IdentityPage extends WebPageImpl {

	/** The identity to show trust relationships of. */
	private final Identity identity;


	/**
	 * Creates a new trust-relationship web page.
	 * 
	 * @param myWebInterface A reference to the WebInterface which created the page, used to get resources the page needs. 
	 * @param myRequest The request sent by the user.
	 * @param identity The identity
	 * @param givenTrusts The trusts having the given identity as truster
	 * @param receivedTrusts The trusts having the given identity as trustee
	 * @throws UnknownIdentityException 
	 */
	public IdentityPage(WebInterface myWebInterface, HTTPRequest myRequest) throws UnknownIdentityException {
		super(myWebInterface, myRequest);
		identity = wot.getIdentityByID(request.getPartAsString("id", 128)); 
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see WebPage#make()
	 */
	public void make() {
		synchronized(identity) {
			makeURIBox();
			makeServicesBox();

			synchronized(wot) {
				HTMLNode trusteeTrustsNode = addContentBox("Identities that '" + identity.getNickname() + "' trusts");
				
				HTMLNode trustersTable = trusteeTrustsNode.addChild("table");
				HTMLNode trustersTableHeader = trustersTable.addChild("tr");
				trustersTableHeader.addChild("th", "Nickname");
				trustersTableHeader.addChild("th", "Identity");
				trustersTableHeader.addChild("th", "Value");
				trustersTableHeader.addChild("th", "Comment");
				
				for (Trust trust : wot.getGivenTrusts(identity)) {
					HTMLNode trustRow = trustersTable.addChild("tr");
					Identity trustee = trust.getTrustee();
					trustRow.addChild("td").addChild("a", "href", "?ShowIdentity&id=" + trustee.getID(), trustee.getNickname());
					trustRow.addChild("td", trustee.getID());
					trustRow.addChild("td", "align", "right", Byte.toString(trust.getValue()));
					trustRow.addChild("td", trust.getComment());
				}
		
				HTMLNode trusterTrustsNode = addContentBox("Identities that trust '" + identity.getNickname() + "'");
		
				HTMLNode trusteesTable = trusterTrustsNode.addChild("table");
				HTMLNode trusteesTableHeader = trusteesTable.addChild("tr");
				trusteesTableHeader.addChild("th", "Nickname");
				trusteesTableHeader.addChild("th", "Identity");
				trusteesTableHeader.addChild("th", "Value");
				trusteesTableHeader.addChild("th", "Comment");
		
				for (Trust trust : wot.getReceivedTrusts(identity)) {
					HTMLNode trustRow = trusteesTable.addChild("tr");
					Identity truster = trust.getTruster();
					trustRow.addChild("td").addChild("a", "href", "?ShowIdentity&id=" + truster.getID(), truster.getNickname());
					trustRow.addChild("td", truster.getID());
					trustRow.addChild("td", "align", "right", Byte.toString(trust.getValue()));
					trustRow.addChild("td", trust.getComment());
				}
			}
		}
	}
	
	private void makeURIBox() {
		HTMLNode boxContent = addContentBox("Reference of identity '" + identity.getNickname() + "'");
		boxContent.addChild("p", "The following Freenet URI is a reference to this identity. If you want to tell other people about this identity, give the URI to them: ");
		boxContent.addChild("p", identity.getRequestURI().toString());
	}
	
	private void makeServicesBox() {
		HTMLNode boxContent = addContentBox("Services of identity '" + identity.getNickname() + "'");
		Iterator<String> iter = identity.getContexts().iterator();
		StringBuilder contexts = new StringBuilder(128);
		while(iter.hasNext()) {
			contexts.append(iter.next());
			if(iter.hasNext())
				contexts.append(", ");
		}
		boxContent.addChild("p", contexts.toString());
	}

}
