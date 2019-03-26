# MyRTS

MyRTS is a multi player Real Time Strategy game prototype written in Java using libgdx library. The game is currently in very early stage but some core features have been implemented already like giving simple commands to units and synchronizing the game over network using simultaneous simulations with lockstep-like model. This kind of architectural style has been used in many classic RTS games like Age of Empires.

The implemented engine is partly based on these articles:
- https://www.gamasutra.com/view/feature/131503/1500_archers_on_a_288_network_.php
- https://www.forrestthewoods.com/blog/synchronous_rts_engines_and_a_tale_of_desyncs/

One of the biggest change is that this engine does not use peer-to-peer connections, but client-server model.

The game engine uses variable physics in singleplayer mode for smooth animations and fixed physics (30fps) in multiplayer mode to keep the simulations in sync.

Gameplay demo: https://www.youtube.com/watch?v=OEy6PhbeE_Y
