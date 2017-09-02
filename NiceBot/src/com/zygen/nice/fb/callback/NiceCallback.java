package com.zygen.nice.fb.callback;

import java.util.List;
import java.util.Map;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.aurasphere.botmill.core.base.BotMillServlet;
import co.aurasphere.botmill.fb.FbBotMillContext;
import co.aurasphere.botmill.fb.internal.util.json.FbBotMillJsonUtils;
import co.aurasphere.botmill.fb.internal.util.network.FbBotMillNetworkConstants;
import co.aurasphere.botmill.fb.model.incoming.MessageEnvelope;
import co.aurasphere.botmill.fb.model.incoming.MessengerCallback;
import co.aurasphere.botmill.fb.model.incoming.MessengerCallbackEntry;
import co.aurasphere.botmill.fb.model.incoming.handler.IncomingToOutgoingMessageHandler;

/**
 * Servlet implementation class NiceCallback
 */
public class NiceCallback extends BotMillServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(NiceCallback.class);    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NiceCallback() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Retrieves GET parameters.
		String validationToken = FbBotMillContext.getInstance()
				.getValidationToken();
		Map<String, String[]> parameters = req.getParameterMap();
		String hubMode = safeUnwrapGetParameters(parameters
				.get(FbBotMillNetworkConstants.HUB_MODE_PARAMETER));
		String hubToken = safeUnwrapGetParameters(parameters
				.get(FbBotMillNetworkConstants.HUB_VERIFY_TOKEN_PARAMETER));
		String hubChallenge = safeUnwrapGetParameters(parameters
				.get(FbBotMillNetworkConstants.HUB_CHALLENGE_PARAMETER));

		// Checks parameters and responds according to that.
		if (hubMode.equals(FbBotMillNetworkConstants.HUB_MODE_SUBSCRIBE)
				&& hubToken.equals(validationToken)) {
			//LOGGER.info("Subscription OK.");
			resp.setStatus(200);
			resp.setContentType("text/plain");
			resp.getWriter().write(hubChallenge);
		} else {
			//LOGGER.warn("GET received is not a subscription or wrong validation token. Ensure you have set the correct validation token using FbBotMillContext.getInstance().setup(String, String).");
			resp.sendError(403);
		}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		//logger.trace("POST received!");
		MessengerCallback callback = new MessengerCallback();

		// Extrapolates and logs the JSON for debugging.
		String json = readerToString(req.getReader());
		LOGGER.debug("JSON input: " + json);

		// Parses the request as a MessengerCallback.
		try {
			callback = FbBotMillJsonUtils.fromJson(json, MessengerCallback.class);
		} catch (Exception e) {
			LOGGER.error("Error during MessengerCallback parsing: ", e);
			return;
		}

		// If the received POST is a MessengerCallback, it forwards the last
		// envelope of all the callbacks received to the registered bots.
		if (callback != null) {
			List<MessengerCallbackEntry> callbackEntries = callback.getEntry();
			if (callbackEntries != null) {
				for (MessengerCallbackEntry entry : callbackEntries) {
					List<MessageEnvelope> envelopes = entry.getMessaging();
					if (envelopes != null) {
						MessageEnvelope lastEnvelope = envelopes.get(envelopes.size() - 1);
						IncomingToOutgoingMessageHandler.getInstance().process(lastEnvelope);
					}
				}
			}
		}
		// Always set to ok.
		resp.setStatus(HttpServletResponse.SC_OK);
	}


	/**
	 * Method which returns the first String in a String array if present or the
	 * empty String otherwise. Used to unwrap the GET arguments from an
	 * {@link HttpServletRequest#getParameterMap()} which returns a String array
	 * for each GET parameter.
	 * 
	 * @param parameter
	 *            the String array to unwrap.
	 * @return the first String of the array if found or the empty String
	 *         otherwise.
	 */
	private static String safeUnwrapGetParameters(String[] parameter) {
		if (parameter == null || parameter[0] == null) {
			return "";
		}
		return parameter[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FbBotMillServlet []";
	}

}
