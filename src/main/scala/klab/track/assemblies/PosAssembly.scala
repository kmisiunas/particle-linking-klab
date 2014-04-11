package klab.track.assemblies

/**
 * == Assembly of Pos for track construction ==
 *
 * === Description ===
 *
 *
 * === Parameters ===
 * The reconstruction of track from snapshots relied on assumptions about the track.
 * Consider:
 *  - Maximum displacement - after this the track will be considered to be new
 *  - Connect lost frames - join tracks if there was finite number of frames lost in between
 *
 * === Features ===
 *  - Operates on all type of Pos objects
 *
 * === Theory ===
 * Many of the ideas were inspired by a "track.m" MatLab code taken from [http://physics.georgetown.edu/matlab/code.html]
 *
 * Given the positions for n particles at time t(i), and m possible new positions at time t(i+1),
 * this function considers all possible identifications of the n old positions with the m new positions,
 * and chooses that identification which results in the minimal total squared displacement.
 * Those identifications which don't associate a new position within maxdisp of an old position ( particle loss )
 * penalize the total squared displacement by maxdisp^2^. For non-interacting Brownian particles with the
 * same diffusivity, this algorithm will produce the most probable set of identifications
 * ( provided maxdisp >> RMS displacement between frames ). In practice it works reasonably well for
 * systems with oscillatory, ballistic, correlated and random hopping motion, so long as single time step
 * displacements are reasonably small.  NB: multidimensional functionality is intended to facilitate tracking
 * when additional information regarding target identity is available (e.g. size or  color).  At present,
 * this information should be rescaled by the user to have a comparable or smaller (measurement) variance than
 * the spatial displacements.
 *
 * Procedure:
 *  - Sort Pos according to time
 *  - Do trivial connections first
 *
 *  === Alternative projects ===
 *   - ptvlab.blogspot.co.uk - full featured particle velocimetry software, not specialised to diffusion problems.
 *   -
 *
 *
 * Created by k.misiunas@gmail.com on 09/12/2013.
 */

class PosAssembly {


}
