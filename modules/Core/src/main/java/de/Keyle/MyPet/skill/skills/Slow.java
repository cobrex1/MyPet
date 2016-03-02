/*
 * This file is part of mypet
 *
 * Copyright (C) 2011-2016 Keyle
 * mypet is licensed under the GNU Lesser General Public License.
 *
 * mypet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mypet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.skill.skills;

import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.skill.ActiveSkill;
import de.Keyle.MyPet.api.skill.SkillInfo;
import de.Keyle.MyPet.api.skill.SkillInstance;
import de.Keyle.MyPet.api.skill.skills.SlowInfo;
import de.Keyle.MyPet.api.util.locale.Translation;
import de.keyle.knbt.TagInt;
import de.keyle.knbt.TagString;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Slow extends SlowInfo implements SkillInstance, ActiveSkill {
    private static Random random = new Random();
    private MyPet myPet;

    public Slow(boolean addedByInheritance) {
        super(addedByInheritance);
    }

    public void setMyPet(MyPet myPet) {
        this.myPet = myPet;
    }

    public MyPet getMyPet() {
        return myPet;
    }

    public boolean isActive() {
        return chance > 0 && duration > 0;
    }

    public void upgrade(SkillInfo upgrade, boolean quiet) {
        if (upgrade instanceof SlowInfo) {
            boolean valuesEdit = false;
            if (upgrade.getProperties().getCompoundData().containsKey("chance")) {
                if (!upgrade.getProperties().getCompoundData().containsKey("addset_chance") || upgrade.getProperties().getAs("addset_chance", TagString.class).getStringData().equals("add")) {
                    chance += upgrade.getProperties().getAs("chance", TagInt.class).getIntData();
                } else {
                    chance = upgrade.getProperties().getAs("chance", TagInt.class).getIntData();
                }
                valuesEdit = true;
            }
            if (upgrade.getProperties().getCompoundData().containsKey("duration")) {
                if (!upgrade.getProperties().getCompoundData().containsKey("addset_duration") || upgrade.getProperties().getAs("addset_duration", TagString.class).getStringData().equals("add")) {
                    duration += upgrade.getProperties().getAs("duration", TagInt.class).getIntData();
                } else {
                    duration = upgrade.getProperties().getAs("duration", TagInt.class).getIntData();
                }
                valuesEdit = true;
            }
            chance = Math.min(chance, 100);
            if (!quiet && valuesEdit) {
                myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Skill.Slow.Upgrade", myPet.getOwner().getLanguage()), myPet.getPetName(), chance, duration));
            }
        }
    }

    public String getFormattedValue() {
        return chance + "% -> " + duration + "sec";
    }

    public void reset() {
        chance = 0;
        duration = 0;
    }

    public boolean activate() {
        return random.nextDouble() <= chance / 100.;
    }

    public int getDuration() {
        return duration;
    }

    public void slowTarget(LivingEntity target) {
        PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, getDuration() * 20, 1, false);
        target.addPotionEffect(effect);
    }

    @Override
    public SkillInstance cloneSkill() {
        Slow newSkill = new Slow(this.isAddedByInheritance());
        newSkill.setProperties(getProperties());
        return newSkill;
    }
}