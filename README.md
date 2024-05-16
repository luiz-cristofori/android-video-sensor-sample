# [Android] Android Video Sensor App Sample

About
=============
This is a sample video project using some of the device sensor following these rules:
- Loads and plays a video file after launch. Video file
- (http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4)
- Using the user's location, a change of 10 meters of the current and previous location will
- reset the video and replay from the start.
- A shake of the device should pause the video.
- Using gyroscope events, rotation along the z-axis should be able to control the current time
  where the video is playing.
- While rotation along the x-axis should control the volume of the sound