# pandero
Pandero audio player - native Anroid audio player written in Kotlin

Tutorial: https://www.youtube.com/watch?v=HsSIgjraJq0&list=PL0pXjGnY7POQCLkvT6jRpCDGnBZLe2HGW&index=2

# Initial steps

## Project
- create an empty activity

## Splashscreen
- Under `Gradle Scripts` / build.gradle.kts (Module :app) add
   - dependencies { implementation("androidx.core:core-splashscreen:1.0.1") }
   - replace with `implementation(libs.androidx.core.splashscreen)` via IDE completion
   - sync now to update references

## Logo
- Under /res/drawable rightclick, new, Vector asset, local file
- load svg and set it to "ic_logo" with 50px/50px


# Useful Links

- PR for MediaButtons on audiobooshelf app: https://github.com/advplyr/audiobookshelf-app/pull/1218/files
- Background playback with media3 (exoplayer): https://developer.android.com/media/media3/session/background-playback?hl=de
- responding to mediabuttons: https://stackoverflow.com/questions/77149675/responding-to-media-button-events-mediasession-vs-mediabuttonreceiver

# MediaButton hint



The purpose of the MediaButtonReceiver in the context of your application depends on the Android API versions you are targeting. Since you mentioned that you only support Android API versions 21 & above, the MediaButtonReceiver may not have a purpose for your specific use case.

In API versions 21 & above, media button events are typically handled by the media session. The media session is responsible for managing media playback & handling media button events. The media session can be set up & controlled using the MediaSessionCompat class from the Android Support Library.

The MediaButtonReceiver is primarily used as a helper for handling media button events on API versions prior to 21. It is designed to receive the android.intent.action.MEDIA_BUTTON broadcast and deliver it to the appropriate media session or media session callback. By including the MediaButtonReceiver in your manifest file, you ensure that media button events are properly routed to your app on older Android versions.

Since you only support API versions 21 & above, the media session should be sufficient for handling media button events in your application. You can register a MediaSessionCompat.Callback to receive media button events & handle them accordingly.

Here's an example of how you can set up a media session & handle media button events:
```kotlin

// Create a MediaSessionCompat instance
MediaSessionCompat mediaSession = new MediaSessionCompat(context, "YourMediaSessionTag");

// Set a MediaSessionCompat.Callback to handle media button events
mediaSession.setCallback(new MediaSessionCompat.Callback() {
@Override
public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
// Handle media button events here
KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
// Handle the media button press
if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
// Handle play/pause button
} else if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT) {
// Handle next button
} else if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
// Handle previous button
}
}
return super.onMediaButtonEvent(mediaButtonIntent);
}
});

// Start the media session
mediaSession.setActive(true);
```

By implementing the onMediaButtonEvent method in the MediaSessionCompat.Callback, you can handle different media button events such as play/pause, next, & previous. This approach allows you to handle media button events directly within the media session without the need for the MediaButtonReceiver.
