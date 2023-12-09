# POT-SCRAPE

## Goal

* **Download images from hotpot.ai**
* It worked on 2023-12-03 with ~90% of ~1400 images being downloaded
  * some issues arose with images that were not available on the aws server

## Usage

  * Fill the config gaps marked by `TODO_CONFIG`.
  * Run.


## What it does

* Login
* Go to the _art-gallery_ and read all `artId`
* For each `artid` 
  * Check if the image already `exists()` in the download folder
    * If not, download the image
* Return to the _art-gallery_
* For each `artid`
  * Check if the image already `exists()` in the download folder
    * If yes, mark the image for deletion
    * After marking 100 images send a DELETE request.
