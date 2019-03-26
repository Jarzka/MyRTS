# MyRTS

MyRTS is a multi player Real Time Strategy game prototype written in Java using libgdx library. The game is currently in very early stage but some core features have been implemented already like giving simple commands to units and synchronizing the game over network.

The game uses the same architecture style that was used in Age of Empires: every player is running individual simulation and these simulations are completely deterministic. Only player inputs are sent to other players over the network. Inputs are synced using so called "communication turns" (SimTick). Finally, all players compute md5 hash from their game state every second and send it to the server. The server checks that all simulations are in sync by using the given hash values.

The implemented engine is partly based on these articles:
- https://www.gamasutra.com/view/feature/131503/1500_archers_on_a_288_network_.php
- https://www.forrestthewoods.com/blog/synchronous_rts_engines_and_a_tale_of_desyncs/

One of the biggest change is that this engine does not use peer-to-peer connections, but client-server model.

The game engine uses variable physics in singleplayer mode for smooth animations and fixed physics (30fps) in multiplayer mode to keep the simulations in sync.

Gameplay demo: https://www.youtube.com/watch?v=OEy6PhbeE_Y
