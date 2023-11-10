# Bread Heads Inventory Manager

A simple app to manage a list of inventories.

Currently an early WIP. Basic list functionality is in place. Tag management is implemented on the backend but currently not functional in-app pending UI implementation. Attaching photos from galleries and retrieving them from Firestore is functional. We are working on expanding features to include:

* Better photo management
* Implementation of tags
* User profiles
* Revamped and improved UI with more visual clarity

For more information, please see current status in [our backlog](https://github.com/orgs/CMPUT301F23T09/projects/1) or [read the documentation on our wiki](https://github.com/CMPUT301F23T09/Bread-Heads/wiki).

## Setup

This app hooks into Firebase and uses both a Firestore database and an associated Firebase Storage.

Compiling this project into a usable app requires hooking it up to a Firebase database via a `google-services.json` file. The `google-services.json` we use is currently included for ease of building. To use your own Firebase database, download its `google-services.json` file and add it to `/app/app/`, replacing the file already there.

## Attributions

This app was developed for CMPUT 301 at the University of Alberta in Fall 2023, based off many provided resources and user stories. [Team information](https://github.com/CMPUT301F23T09/Bread-Heads/blob/main/doc/team.txt). Code adapted from other sources is cited where it appears, but in general we avoided this.
