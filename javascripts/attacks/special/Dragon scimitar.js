importClass(org.maxgamer.rs.model.entity.mob.combat.MeleeAttack);
importClass(org.maxgamer.rs.model.entity.mob.persona.Persona);
importClass(org.maxgamer.rs.model.skill.prayer.PrayerGroup);
importClass(org.maxgamer.rs.model.skill.prayer.PrayerType);
/**
 * Optional - Generate the damage that should be dealt to the target via the damage object.
 * No damage should ever be applied here, as that would skip the Event system, animations,
 * delays and damage tracking.  This method defaults to a single random melee strike if the
 * method isn't specified.
 * @return boolean (default true). If the preparation fails, returning false will end the attack
 * 		   without dealing damage. Not dealing any damage will have the same effect.
 */
function prepare(attacker, target, damage){
	damage.add(MeleeAttack.roll(attacker, target, 1.15, 1.0));

}

/**
 * Optional - Apply the damage that was generated by prepare(). This damage has been pushed through
 * the event system, the animation has been performed and the damage logged. By default, this method
 * will apply the damage (damage.apply(target)) and grant experience to the attacker. This method is
 * the only method that may call wait(int ticks).  Calling wait() here will NOT be called as part of the player's
 * action - The attack is now separated from any game actions. Eg, the projectile is already flying,
 * the attacker has no responsibility to continue the action.
 */
function perform(attacker, target, damage){
	attacker.animate(12031);
	attacker.graphics(347);//TODO: Not correct, but close
	if (target instanceof Persona) {
		target.say("Boohoohoo...Why...");
		var prayers = [PrayerType.PROTECT_FROM_MAGIC, PrayerType.PROTECT_FROM_MELEE, PrayerType.PROTECT_FROM_MISSILES];
		target.getPrayer().setEnabled(false, prayers);
	} else {
		target.say("Nope");
	}
	//TODO: 5 seconds unable to use prot prayers 
	damage.apply(attacker);


}

/**
 * Optional - Take the consumable items from the attacker. Consumables may be runes, special attack
 * energy, equipment (arrows/knives), or anything else.  Returning false will fail the attack. Not
 * returning or returning true signifies that the attacker had all required consumables to attack
 */
function takeConsumables(attacker){
	var e = attacker.getAttackEnergy();
	if (e < 55) {
		attacker.sendMessage("You do not have enough special attack energy.");
		return false;
	}
	attacker.setAttackEnergy(e - 55);

	return true;
}