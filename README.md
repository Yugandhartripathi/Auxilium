# Auxilium

### Problem and motivation 
This world is not a safe place. Every day every minute someone meets an accident that person can be you or someone dear to you. 
But the suffering person and the onlookers often don't know the correct way to call for help(area based help line numbers) or simply act ignorant because of the whole ordeal of calling the hospital(can be expanded to firefighter etc services), explaining the situation, and providing the parameters required by the helpers like location and surroundings(landmark, locality).
Even if someone cares enough and takes the initiative the next problem is requirement of family authorisation for various hospital and police related formalities.

### Solution:
An app that makes it easier for people to show their caring and proactive side by removing most of the hassle and cumbersome tasks mentioned in the problem.

### Key Features:
All of these require just simple taps on power and/or volume buttons.
1) Transferring location of the incident to the nearest responsible authorities.
2) Contacting someone's guardians by using their mobile's power button or volume key combination. Thus even if someone is out cold we can use their mobile and contact their family without actually revealing the MobileNumber and other details to strangers.

### Tech Stack: 
- Android Studio
- Java
- Firebase API(for real-time database and analytics services)
- Geo-Location API(android gms play services) for location data(latitude, longitude, altitude).

### Challenges and short comings:
Assumption that mobile phone after accident doesn't lose power(mere screen damage won't hurt)
Dependency on user's integrity as there is no way to verify the incoming alerts as real or fake. 
(A possible solution would be to ask for official id info for first time registration of users for accountability).
