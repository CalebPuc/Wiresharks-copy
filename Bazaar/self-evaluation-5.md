1. Legality check for the first referee call:

'RuleBook.java' contains a method called 'isLegalExchangeSequence(TurnState state, List<ExchangeRequest> requests)' from lines 63 to 92. This method ensures that each exchange uses a known equation, can be applied in the requested direction, and is satisfiable by the bank.

2. Legality check for the second referee call

'RuleBook.java' contains a method called 'isLegalCardPurchase(TurnState state, List<ExchangeRequest> exchanges, Cards requested)' from lines 104 to 122 that ensures that the card is in the visible table and the player can afford it after the applied exchanges.

3. Rule-book functionality

The method 'isLegalCardPurchase' from 'RuleBook.java' verifies whether a player may buy a card.
The method 'computeCardPoints(Cards card, Map<PebbleColor, Integer> wallet)' in lines 133 to 145 of 'RuleBook.java' scores a purchase made by a player
The method 'isGameOver()' from 'GameState.java' determines if the game is over. We need to swap this funtionality to 'RuleBook.java'.

4. Unit tests

We did not implement any unit tests for this milestone.