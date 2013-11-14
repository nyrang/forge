package forge.card.ability.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import forge.Card;
import forge.CounterType;
import forge.card.ability.AbilityUtils;
import forge.card.ability.SpellAbilityEffect;
import forge.card.spellability.SpellAbility;
import forge.gui.GuiChoose;

public class CountersMoveEffect extends SpellAbilityEffect {

    @Override
    protected String getStackDescription(SpellAbility sa) {
        final StringBuilder sb = new StringBuilder();

        Card source = null;
        List<Card> srcCards = getDefinedCardsOrTargeted(sa, "Source");
        
        if (srcCards.size() > 0) {
            source = srcCards.get(0);
        }
        final List<Card> tgtCards = getDefinedCardsOrTargeted(sa);
        final String countername = sa.getParam("CounterType");
        final int amount = AbilityUtils.calculateAmount(sa.getSourceCard(), sa.getParam("CounterNum"), sa);

        sb.append("Move ");
        if ("Any".matches(countername)) {
            if (amount == 1) {
                sb.append("a counter");
            } else {
                sb.append(amount).append(" ").append(" counter");
            }
        } else {   
            sb.append(amount).append(" ").append(countername).append(" counter");
        }
        if (amount != 1) {
            sb.append("s");
        }
        sb.append(" from ");
        sb.append(source).append(" to ").append(tgtCards.get(0));

        sb.append(".");
        return sb.toString();
    }

    @Override
    public void resolve(SpellAbility sa) {
        final Card host = sa.getSourceCard();
        final String counterName = sa.getParam("CounterType");
        int amount = 0;
        if (!sa.getParam("CounterNum").equals("All")) {
            amount = AbilityUtils.calculateAmount(host, sa.getParam("CounterNum"), sa);
        }
        
        CounterType cType = null;
        try {
            cType = AbilityUtils.getCounterType(counterName, sa);
        } catch (Exception e) {
            if (!counterName.matches("Any")) {
                System.out.println("Counter type doesn't match, nor does an SVar exist with the type name.");
                return;
            }
        }

        Card source = null;
        List<Card> srcCards = getDefinedCardsOrTargeted(sa, "Source");
        if (srcCards.size() > 0) {
            source = srcCards.get(0);
        }
        if (sa.getParam("CounterNum").equals("All")) {
            amount = source.getCounters(cType);
        }
        List<Card> tgtCards = getDefinedCardsOrTargeted(sa);

        for (final Card dest : tgtCards) {
            if (null != source && null != dest) {
                // rule 121.5: If the first and second objects are the same object, nothing happens
                if (source.equals(dest)) {
                    continue;
                }
                if (!"Any".matches(counterName)) {
                    if (dest.canReceiveCounters(cType)
                            && source.getCounters(cType) >= amount) {
                        dest.addCounter(cType, amount, true);
                        source.subtractCounter(cType, amount);
                    }
                } else {
                    if (dest.hasKeyword("CARDNAME can't have counters placed on it.")) {
                        return;
                    }
                    boolean check = false;
                    for (final Card c : dest.getController().getCreaturesInPlay()) {//Melira, Sylvok Outcast
                        if (c.hasKeyword("Creatures you control can't have -1/-1 counters placed on them.")) {
                            check = true;
                        }
                    }
                    while (amount > 0 && source.hasCounters()) {
                        final Map<CounterType, Integer> tgtCounters = source.getCounters();
                        CounterType chosenType = null;
                        int chosenAmount;
                        if (sa.getActivatingPlayer().isHuman()) {
                            final ArrayList<CounterType> typeChoices = new ArrayList<CounterType>();
                            // get types of counters
                            for (CounterType key : tgtCounters.keySet()) {
                                if (tgtCounters.get(key) > 0 && !(key == CounterType.M1M1 && check)) {
                                    typeChoices.add(key);
                                }
                            }
                            if (typeChoices.isEmpty()) {
                                return;
                            }
                            if (typeChoices.size() > 1) {
                                String prompt = "Select type counters to remove";
                                chosenType = GuiChoose.one(prompt, typeChoices);
                            } else {
                                chosenType = typeChoices.get(0);
                            }
                            chosenAmount = tgtCounters.get(chosenType);
                            if (chosenAmount > amount) {
                                chosenAmount = amount;
                            }
                            // make list of amount choices
                            if (chosenAmount > 1) {
                                final List<Integer> choices = new ArrayList<Integer>();
                                for (int i = 1; i <= chosenAmount; i++) {
                                    choices.add(Integer.valueOf(i));
                                }
                                String prompt = "Select the number of " + chosenType.getName() + " counters to remove";
                                chosenAmount = GuiChoose.one(prompt, choices);
                            }
                        } else {
                            for (Object key : tgtCounters.keySet()) {
                                if (tgtCounters.get(key) > 0) {
                                    chosenType = (CounterType) key;
                                    break;
                                }
                            }
                            // subtract all of selected type
                            chosenAmount = tgtCounters.get(chosenType);
                            if (chosenAmount > amount) {
                                chosenAmount = amount;
                            }
                        }
                        dest.addCounter(chosenType, chosenAmount, true);
                        source.subtractCounter(chosenType, chosenAmount);
                        amount -= chosenAmount;
                    }
                }
            }
        }
    } // moveCounterResolve
}