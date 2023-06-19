# Setup

## Device Requirements

* Amazon FireOS version 7 or greater

## Device Configuration

### Disable Screensaver

* Go to `Settings > Display & Sounds`, then `Screensaver`
* Scroll down to the bottom of the list and click `Start Time`
* Scroll down to and select "Never"
* Click "back" (<-) twice to return to settings menu or "home" to exit

### Enable Developer Options

* Go to `Settings > My Fire TV`, then `About`
* The first item in the "About" screen should list the device type name, for example "Fire TV Stick Lite".  Click this list item repeatedly until the system confirms that developer options have been enabled.  If you click again after this the screen should read "No Need, You Are Already a Developer"
* Click "back" (<-) twice to return to settings menu or "home" to exit


## App Setup

### Install Downloader App

* Go to `Appstore`
* Search for "Downloader" and select (Orange Logo)
* From the app overview screen locate and click "Download"
* After install is complete choose "Open" to lauch downloader
  * When prompted to "Allow Downloader to access photos, ...", select "Allow"

### Install Signage App

* From Downloader App (above) select the `Browser` option on the left menu
* Position the cursor in the URL/address bar and click, then find and click `[clear]` on the on-screen keyboard
* Enter the address `github.com/game7/signage` and click `[go]`
* When the Github page titled `game7 / signage` appears, scroll down and locate the section named "Releases".  Click the title `Releases`
* Move the cursor to the gold menu button in the top-right corner of the screen and click, then choose "Add current page to favorites".  When the popup dialog appears click `[ Save ]`
    * adding this page to favorites will make it easier to perform future updates
* Move the cursor back to the main browser window and locate the upper-most listing within the releases page.  It will have a large label with the latest version number - for example v0.2.1.  Beneath this latest listing locate the link for the APK package which should have a name beginning with *sportified.signage* and ending with *.apk*.  Click this link to download the package.
* If this is the first time installing the application you may receive a screen warning of download permissions.  If so click the `[ Settings ]` button to grant permissions
    * Upon pressing `[ Settings ]` the *My Fire TV* setttings screen will appear.  
    * Click `Install unknown apps`, then select and click "Downloader" so that it is toggled to "ON"
    * Press the "back" (<-) button twice to return to *Downloader*
* Click the gold `[ Install ]` button to install the *Signage* apk.  
    * When the white confirmation screen appears again click "INSTALL"
    * When the "App installed" screen appears click "DONE"
* When returned to the dark-gray "Status" screen select and click `[ Done ]`
* Finally, click the "Home" button on the remote to return to the main screen

### Run and Configure the Signage App

* From the FireTV home screen locate and click the "Apps" icon, then go to "My Apps"
* Navigate to the bottom of the list and locate the Signage app -- it currently uses the green default Android logo.  Click the app icon to start.
* On the first run the Signage application will display a 6-character code that must be entered into the *Screens* area of your Sportified admin console
* To access the Signage app settings click the "back" (<-) button and select `[ Settings ]`
    * if your display is mounted in portrait/vertical orientation, use the remote to select and click the `[ TOP ]` button that is located near the top of your display
    * if you would like the Signage app to automatically launch following a device restart, use the remote to select and click the `[ LAUNCH ]` button on the top of the settings screen, then select and click the switch that is labelled "Automatically Launch..."
* To return to the Signage main screen simply click the "back" (<-) button on the remote.
* You're good to go!
