package com.zygen.nice.fb.callback;

import java.util.ArrayList;
import java.util.List;

import co.aurasphere.botmill.core.annotation.BotConfiguration;
import co.aurasphere.botmill.fb.FbBotConfiguration;
import co.aurasphere.botmill.fb.api.MessengerProfileApi;
import co.aurasphere.botmill.fb.model.api.messengerprofile.HomeUrl;
import co.aurasphere.botmill.fb.model.api.messengerprofile.persistentmenu.CallToActionNested;
import co.aurasphere.botmill.fb.model.api.messengerprofile.persistentmenu.PersistentMenu;
import co.aurasphere.botmill.fb.model.outcoming.factory.ButtonFactory;
import co.aurasphere.botmill.fb.model.outcoming.template.button.WebViewHeightRatioType;
import co.aurasphere.botmill.fb.model.outcoming.template.button.WebViewShareButton;

@BotConfiguration
public class MyBotConfiguration extends FbBotConfiguration {

    public MyBotConfiguration() {
    
		MessengerProfileApi.setGetStartedButton("get_started");
		MessengerProfileApi.setGreetingMessage("Hello!");
		
		List<PersistentMenu> persistentMenus = new ArrayList<PersistentMenu>();
		PersistentMenu persistentMenu = new PersistentMenu("default", false);
		
		persistentMenu.addCallToAction(ButtonFactory.createPostbackButton("Menu 1", "menu1"));
		persistentMenu.addCallToAction(ButtonFactory.createPostbackButton("Menu 2", "menu2"));
		
		CallToActionNested theNestedMenu = new CallToActionNested("Menu 3 Nested");
		theNestedMenu.addCallToActionButton(ButtonFactory.createPostbackButton("Nested1", "nested1"));
		theNestedMenu.addCallToActionButton(ButtonFactory.createPostbackButton("Nested2", "nested2"));
		theNestedMenu.addCallToActionButton(ButtonFactory.createPostbackButton("Nested3", "nested3"));
		persistentMenu.addCallToAction(theNestedMenu);
		
		persistentMenus.add(persistentMenu);
		
		MessengerProfileApi.setPersistentMenus(persistentMenus);
		
		HomeUrl homeUrl = new HomeUrl();
		homeUrl.setInTest(true);
		homeUrl.setUrl("https://extensionlink.co");
		homeUrl.setWebviewHeightRatio(WebViewHeightRatioType.TALL);
		homeUrl.setWebviewShareButton(WebViewShareButton.SHOW);
		
		MessengerProfileApi.setHomeUrl(homeUrl);
		
    }
    
}
