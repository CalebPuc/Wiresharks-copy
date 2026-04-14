## Self-Evaluation Form for Milestone 5

Indicate below each bullet which piece of your code takes care of each task:

1. where is the functionality (signature, purpose statement) that
   checks the legality of the result of the first call from the referee
   to the player

2. where is the functionality (signature, purpose statement) that
   checks the legality of the result of the second call from the referee
   to the player

3. the rules of the game also specify whether a player may buy a card, 
   how to score the purchase of a card, and how to determine whether
   the game is over.

   - Does your rule-book code include these pieces of functionality?

   - where?

   - Did you revise `game-state` to rely on rule-book instead of
     implementing the check directly?

4. Where are the unit tests for the two explicitly required pieces of
   functionality? Specifically, point to unit tests that

    - with an empty bank and a request for a pebble

    - confirms that requesting from a non-empty bank is okay

    - covers a trade request that would put the player into ``debt''

    - covers a trade request that would put the bank into ``debt''

    - for at least one trade that succeeds.

The ideal feedback for each of these points is a GitHub perma-link to
the range of lines in a specific file or a collection of files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you failed to do any of the above points, say so.
