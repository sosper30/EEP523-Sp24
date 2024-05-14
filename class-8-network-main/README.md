# Networking on Android

Created by: Adrienne Slaughter May 2022
Updated by: Sep Makhsous May 2024


This demo gives examples of how we can do basic networking on Android. 

Depending on what we want to do, we have the following options: 

* Opening a webpage in an app that can handle a URL.
* Display the contents of a URL in a WebView, within our app. 
* Making an HTTP request like we do in Java in general, but on another thread
   * Either an AsyncTask (deprecated, so discouraged to do this going forward)
   * or an Executor/Handler (for UI updating-- Preferred) 

# Things to keep in mind

* Network calls can be expensive (in terms of power/battery life) and time consuming! 
    * Depending on a live network all the time is a bad idea
    * If you have a lot of data to get from the network, try to be smart about requesting data
* Android won't let you make network calls on the main/UI thread. 
    * Make it asynchronous/concurrent
* Pay attention to the user's preferences for using the cellular vs the WiFi network.
    * Be sure that the user can specify this preference
    * Don't ignore it! 
  