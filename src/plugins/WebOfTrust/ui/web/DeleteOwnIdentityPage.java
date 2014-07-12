/* This code is part of WoT, a plugin for Freenet. It is distributed 
 * under the GNU General Public License, version 2 (or at your option
 * any later version). See http://www.gnu.org/ for details of the GPL. */
package plugins.WebOfTrust.ui.web;

import plugins.WebOfTrust.OwnIdentity;
import plugins.WebOfTrust.exceptions.UnknownIdentityException;
import plugins.WebOfTrust.ui.web.WebInterface.LoginWebInterfaceToadlet;
import freenet.clients.http.RedirectException;
import freenet.clients.http.SessionManager.Session;
import freenet.clients.http.ToadletContext;
import freenet.support.HTMLNode;
import freenet.support.api.HTTPRequest;


/**
 * The web interface of the WoT plugin.
 * 
 * @author xor (xor@freenetproject.org)
 * @author Julien Cornuwel (batosai@freenetproject.org)
 */
public class DeleteOwnIdentityPage extends WebPageImpl {
	
	private final OwnIdentity mIdentity;

	/**
	 * @throws RedirectException If the {@link Session} has expired. 
	 */
	public DeleteOwnIdentityPage(WebInterfaceToadlet toadlet, HTTPRequest myRequest, ToadletContext context) throws UnknownIdentityException, RedirectException {
		super(toadlet, myRequest, context, true);
		
		mIdentity = mWebOfTrust.getOwnIdentityByID(mLoggedInOwnIdentityID);
	}

	public void make() {
		if(mRequest.isPartSet("confirm")) {
			try {
				mWebOfTrust.deleteOwnIdentity(mIdentity.getID());
				mToadlet.logOut(mContext);
				
				HTMLNode box = addContentBox(l10n().getString("DeleteOwnIdentityPage.IdentityDeleted.Header"));
				box.addChild("#", l10n().getString("DeleteOwnIdentityPage.IdentityDeleted.Text"));
				
				// Cast because the casted version does not throw RedirectException.
				((LoginWebInterfaceToadlet)mWebInterface.getToadlet(LoginWebInterfaceToadlet.class))
					.makeWebPage(mRequest, mContext).addToPage(this);
			} catch (UnknownIdentityException e) {
				new ErrorPage(mToadlet, mRequest, mContext, e).addToPage(this);
			}
		}
		else
			makeConfirmation();
	}
	
	private void makeConfirmation() {
		HTMLNode box = addContentBox(l10n().getString("DeleteOwnIdentityPage.DeleteIdentityBox.Header"));

		box.addChild(new HTMLNode("p", l10n().getString("DeleteOwnIdentityPage.DeleteIdentityBox.Text1", "nickname", mIdentity.getShortestUniqueNickname())));
		box.addChild(new HTMLNode("p", l10n().getString("DeleteOwnIdentityPage.DeleteIdentityBox.Text2")));

		HTMLNode confirmForm = pr.addFormChild(box, uri.toString(), "DeleteIdentity");

		confirmForm.addChild("input", new String[] { "type", "name", "value" }, new String[] { "submit", "confirm", l10n().getString("DeleteOwnIdentityPage.DeleteIdentityBox.ConfirmButton") });
	}
}
