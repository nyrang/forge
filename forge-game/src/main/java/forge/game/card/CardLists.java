/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.game.card;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.util.MyRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * <p>
 * CardListUtil class.
 * </p>
 * 
 * @author Forge
 * @version $Id$
 */
public class CardLists {
    /**
     * <p>
     * filterToughness.
     * </p>
     * 
     * @param in
     *            a {@link forge.CardList} object.
     * @param atLeastToughness
     *            a int.
     * @return a {@link forge.CardList} object.
     */
    public static List<Card> filterToughness(final List<Card> in, final int atLeastToughness) {
        return CardLists.filter(in, new Predicate<Card>() {
            @Override
            public boolean apply(Card c) {
                return c.getNetDefense() <= atLeastToughness;
            }
        });
    }

    public static final Comparator<Card> ToughnessComparator = new Comparator<Card>() {
        @Override
        public int compare(final Card a, final Card b) {
            return a.getNetDefense() - b.getNetDefense();
        }
    };
    public static final Comparator<Card> PowerComparator = new Comparator<Card>() {
        @Override
        public int compare(final Card a, final Card b) {
            return a.getNetCombatDamage() - b.getNetCombatDamage();
        }
    };
    public static final Comparator<Card> CmcComparatorInv = new Comparator<Card>() {
        @Override
        public int compare(final Card a, final Card b) {
            return b.getCMC() - a.getCMC();
        }
    };

    public static final Comparator<Card> TextLenComparator = new Comparator<Card>() {
        @Override
        public int compare(final Card a, final Card b) {
            final int aLen = a.getText().length();
            final int bLen = b.getText().length();
            return aLen - bLen;
        }
    };
    
    public static final List<Card> emptyList = ImmutableList.of();

    /**
     * <p>
     * Sorts a List<Card> from highest converted mana cost to lowest.
     * </p>
     * 
     * @param list
     *            a {@link forge.CardList} object.
     */
    public static void sortByCmcDesc(final List<Card> list) {
        Collections.sort(list, CmcComparatorInv);
    } // sortCMC

    /**
     * <p>
     * sortAttackLowFirst.
     * </p>
     * 
     * @param list
     *            a {@link forge.CardList} object.
     */
    public static void sortByPowerAsc(final List<Card> list) {
        Collections.sort(list, PowerComparator);
    } // sortAttackLowFirst()

    // the higher the attack the better
    /**
     * <p>
     * sortAttack.
     * </p>
     * 
     * @param list
     *            a {@link forge.CardList} object.
     */
    public static void sortByPowerDesc(final List<Card> list) {
        Collections.sort(list, Collections.reverseOrder(PowerComparator));
    } // sortAttack()


    /**
     * 
     * Given a List<Card> c, return a List<Card> that contains a random amount of cards from c.
     * 
     * @param c
     *            CardList
     * @param amount
     *            int
     * @return CardList
     */
    public static List<Card> getRandomSubList(final List<Card> c, final int amount) {
        if (c.size() < amount) {
            return null;
        }

        final List<Card> cs = Lists.newArrayList(c);

        final List<Card> subList = new ArrayList<Card>();
        while (subList.size() < amount) {
            CardLists.shuffle(cs);
            subList.add(cs.remove(0));
        }
        return subList;
    }

    /**
     * TODO: Write javadoc for this method.
     * @param cardList
     */
    public static void shuffle(List<Card> list) {
        // reseed Random each time we want to Shuffle
        // MyRandom.random = MyRandom.random;
        Collections.shuffle(list, MyRandom.getRandom());
        Collections.shuffle(list, MyRandom.getRandom());
        Collections.shuffle(list, MyRandom.getRandom());
    }

    public static List<Card> filterControlledBy(Iterable<Card> cardList, Player player) {
        return CardLists.filter(cardList, CardPredicates.isController(player));
    }

    public static List<Card> filterControlledBy(Iterable<Card> cardList, List<Player> player) {
        return CardLists.filter(cardList, CardPredicates.isControlledByAnyOf(player));
    }

    public static List<Card> getValidCards(Iterable<Card> cardList, String[] restrictions, Player sourceController, Card source) {
        return CardLists.filter(cardList, CardPredicates.restriction(restrictions, sourceController, source));
    }

    public static List<Card> getValidCards(Iterable<Card> cardList, String restriction, Player sourceController, Card source) {
        return CardLists.filter(cardList, CardPredicates.restriction(restriction.split(","), sourceController, source));
    }

    public static List<Card> getTargetableCards(Iterable<Card> cardList, SpellAbility source) {
        return CardLists.filter(cardList, CardPredicates.isTargetableBy(source));
    }

    public static List<Card> getKeyword(Iterable<Card> cardList, String keyword) {
        return CardLists.filter(cardList, CardPredicates.hasKeyword(keyword));
    }

    public static List<Card> getNotKeyword(Iterable<Card> cardList, String keyword) {
        return CardLists.filter(cardList, Predicates.not(CardPredicates.hasKeyword(keyword)));
    }

    // cardType is like "Land" or "Goblin", returns a new ArrayList<Card> that is a
    // subset of current CardList
    public static List<Card> getNotType(Iterable<Card> cardList, String cardType) {
        return CardLists.filter(cardList, Predicates.not(CardPredicates.isType(cardType)));
    }

    public static List<Card> getType(Iterable<Card> cardList, String cardType) {
        return CardLists.filter(cardList, CardPredicates.isType(cardType));
    }

    public static List<Card> getNotColor(Iterable<Card> cardList, byte color) {
        return CardLists.filter(cardList, Predicates.not(CardPredicates.isColor(color)));
    }

    public static List<Card> getColor(Iterable<Card> cardList, byte color) {
        return CardLists.filter(cardList, CardPredicates.isColor(color));
    }

    /**
     * Create a new list of cards by applying a filter to this one.
     * 
     * @param filt
     *            determines which cards are present in the resulting list
     * 
     * @return a subset of this List<Card> whose items meet the filtering
     *         criteria; may be empty, but never null.
     */
    public static List<Card> filter(Iterable<Card> cardList, Predicate<Card> filt) {
        return Lists.newArrayList(Iterables.filter(cardList, filt));
    }

    public static List<Card> filter(Iterable<Card> cardList, Predicate<Card> f1, Predicate<Card> f2) {
        return Lists.newArrayList(Iterables.filter(Iterables.filter(cardList, f1), f2));
    }    
    
    /**
     * Given a List<Card> cardList, return a List<Card> that are tied for having the highest CMC.
     * 
     * @param cardList          the Card List to be filtered.
     * @return the list of Cards sharing the highest CMC.
     */
    public static List<Card> getCardsWithHighestCMC(Iterable<Card> cardList) {
        final List<Card> tiedForHighest = new ArrayList<Card>();
        int highest = 0;
        for (final Card crd : cardList) {
            int curCmc = crd.isSplitCard() ? Math.max(crd.getCMC(Card.SplitCMCMode.LeftSplitCMC), crd.getCMC(Card.SplitCMCMode.RightSplitCMC)) : crd.getCMC();

            if (curCmc > highest) {
                highest = curCmc;
                tiedForHighest.clear();
            }
            if (curCmc >= highest) {
                tiedForHighest.add(crd);
            }
        }
        return tiedForHighest;
    }

    /**
     * Given a List<Card> cardList, return a List<Card> that are tied for having the lowest CMC.
     * 
     * @param cardList          the Card List to be filtered.
     * @return the list of Cards sharing the lowest CMC.
     */
    public static List<Card> getCardsWithLowestCMC(Iterable<Card> cardList) {
        final List<Card> tiedForLowest = new ArrayList<Card>();
        int lowest = 25;
        for (final Card crd : cardList) {
            int curCmc = crd.isSplitCard() ? Math.min(crd.getCMC(Card.SplitCMCMode.LeftSplitCMC), crd.getCMC(Card.SplitCMCMode.RightSplitCMC)) : crd.getCMC();

            if (curCmc < lowest) {
                lowest = curCmc;
                tiedForLowest.clear();
            }
            if (curCmc <= lowest) {
                tiedForLowest.add(crd);
            }
        }
        return tiedForLowest;
    }

    /**
     * Given a List<Card> cardList, return a int TotalPower.
     * 
     * @param cardList          the Card List to be filtered.
     * @return the total power.
     */
    public static int getTotalPower(Iterable<Card> cardList) {
        int total = 0;
        for (final Card crd : cardList) {
            total += crd.getNetAttack();
        }
        return total;
    }
}
