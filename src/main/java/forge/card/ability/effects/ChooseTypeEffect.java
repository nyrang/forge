package forge.card.ability.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import forge.Card;
import forge.CardLists;
import forge.Constant;
import forge.Singletons;
import forge.card.CardType;
import forge.card.ability.SpellEffect;
import forge.card.cardfactory.CardFactoryUtil;
import forge.card.spellability.SpellAbility;
import forge.card.spellability.Target;
import forge.game.player.Player;
import forge.game.zone.ZoneType;
import forge.gui.GuiChoose;

public class ChooseTypeEffect extends SpellEffect {

    @Override
    protected String getStackDescription(SpellAbility sa) {
        final StringBuilder sb = new StringBuilder();

        for (final Player p : getTargetPlayers(sa)) {
            sb.append(p).append(" ");
        }
        sb.append("chooses a type.");

        return sb.toString();
    }

    @Override
    public void resolve(SpellAbility sa) {
        final Card card = sa.getSourceCard();
        final String type = sa.getParam("Type");
        final ArrayList<String> invalidTypes = new ArrayList<String>();
        if (sa.hasParam("InvalidTypes")) {
            invalidTypes.addAll(Arrays.asList(sa.getParam("InvalidTypes").split(",")));
        }

        final ArrayList<String> validTypes = new ArrayList<String>();
        if (sa.hasParam("ValidTypes")) {
            validTypes.addAll(Arrays.asList(sa.getParam("ValidTypes").split(",")));
        }

        final Target tgt = sa.getTarget();
        final List<Player> tgtPlayers = getTargetPlayers(sa);

        for (final Player p : tgtPlayers) {
            if ((tgt == null) || p.canBeTargetedBy(sa)) {

                if (type.equals("Card")) {
                    if (validTypes.isEmpty()) {
                        validTypes.addAll(Constant.CardTypes.CARD_TYPES);
                    }
                    boolean valid = false;
                    while (!valid) {
                        if (sa.getActivatingPlayer().isHuman()) {
                            final Object o = GuiChoose.one("Choose a card type", validTypes);
                            if (null == o) {
                                return;
                            }
                            final String choice = (String) o;
                            if (CardType.isACardType(choice) && !invalidTypes.contains(choice)) {
                                valid = true;
                                card.setChosenType(choice);
                            }
                        } else {
                            // TODO
                            // computer will need to choose a type
                            // based on whether it needs a creature or land,
                            // otherwise, lib search for most common type left
                            // then, reveal chosenType to Human
                        }
                    }
                } else if (type.equals("Creature")) {
                    String chosenType = "";
                    boolean valid = false;
                    while (!valid) {
                        if (sa.getActivatingPlayer().isHuman()) {
                            final ArrayList<String> validChoices = CardType.getCreatureTypes();
                            for (final String s : invalidTypes) {
                                validChoices.remove(s);
                            }
                            final Object o = GuiChoose.one("Choose a creature type", validChoices);
                            if (null == o) {
                                return;
                            }
                            final String choice = (String) o;
                            if (CardType.isACreatureType(choice) && !invalidTypes.contains(choice)) {
                                valid = true;
                                card.setChosenType(choice);
                            }
                        } else {
                            Player ai = sa.getActivatingPlayer();
                            Player opp = ai.getOpponent();
                            String chosen = "";
                            if (sa.hasParam("AILogic")) {
                                final String logic = sa.getParam("AILogic");
                                if (logic.equals("MostProminentOnBattlefield")) {
                                    chosen = CardFactoryUtil.getMostProminentCreatureType(Singletons.getModel().getGame().getCardsIn(ZoneType.Battlefield));
                                }
                                if (logic.equals("MostProminentComputerControls")) {
                                    chosen = CardFactoryUtil.getMostProminentCreatureType(ai.getCardsIn(ZoneType.Battlefield));
                                }
                                if (logic.equals("MostProminentHumanControls")) {
                                    chosen = CardFactoryUtil.getMostProminentCreatureType(opp.getCardsIn(ZoneType.Battlefield));
                                    if (!CardType.isACreatureType(chosen) || invalidTypes.contains(chosen)) {
                                        chosen = CardFactoryUtil.getMostProminentCreatureType(CardLists.filterControlledBy(Singletons.getModel().getGame().getCardsInGame(), opp));
                                    }
                                }
                                if (logic.equals("MostProminentInComputerDeck")) {
                                    chosen = CardFactoryUtil.getMostProminentCreatureType(CardLists.filterControlledBy(Singletons.getModel().getGame().getCardsInGame(), ai));
                                }
                                if (logic.equals("MostProminentInComputerGraveyard")) {
                                    chosen = CardFactoryUtil.getMostProminentCreatureType(ai.getCardsIn(ZoneType.Graveyard));
                                }
                            }
                            if (!CardType.isACreatureType(chosen) || invalidTypes.contains(chosen)) {
                                chosen = "Sliver";
                            }
                            GuiChoose.one("Computer picked: ", new String[]{chosen});
                            chosenType = chosen;
                        }
                        
                        if (CardType.isACreatureType(chosenType) && !invalidTypes.contains(chosenType)) {
                            valid = true;
                            card.setChosenType(chosenType);
                        }
                    }
                } else if (type.equals("Basic Land")) {
                    boolean valid = false;
                    while (!valid) {
                        if (sa.getActivatingPlayer().isHuman()) {
                            final String choice = GuiChoose.one("Choose a basic land type", CardType.getBasicTypes());
                            if (null == choice) {
                                return;
                            }
                            if (CardType.isABasicLandType(choice) && !invalidTypes.contains(choice)) {
                                valid = true;
                                card.setChosenType(choice);
                            }
                        } else {
                            // TODO AttachAI for ChosenType should have the exact same logic
                            card.setChosenType("Island");
                            valid = true;
                        }
                    }
                } else if (type.equals("Land")) {
                    boolean valid = false;
                    while (!valid) {
                        if (sa.getActivatingPlayer().isHuman()) {
                            final String choice = GuiChoose
                                    .one("Choose a land type", CardType.getLandTypes());
                            if (null == choice) {
                                return;
                            }
                            if (!invalidTypes.contains(choice)) {
                                valid = true;
                                card.setChosenType(choice);
                            }
                        } else {
                            // TODO
                            // computer will need to choose a type
                            card.setChosenType("Island");
                            valid = true;
                        }
                    }
                } // end if-else if
            }
        }
    }

}