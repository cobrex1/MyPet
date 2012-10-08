/*
 * Copyright (C) 2011-2012 Keyle
 *
 * This file is part of MyPet
 *
 * MyPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyPet. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.listeners;

import de.Keyle.MyPet.entity.types.MyPet;
import de.Keyle.MyPet.event.MyPetLevelUpEvent;
import de.Keyle.MyPet.skill.MyPetSkillTree;
import de.Keyle.MyPet.skill.MyPetSkillTreeSkill;
import de.Keyle.MyPet.util.MyPetLanguage;
import de.Keyle.MyPet.util.MyPetUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class MyPetLevelUpListener implements Listener
{
    @EventHandler
    public void onLevelUp(MyPetLevelUpEvent eventMyPet)
    {
        MyPet MPet = eventMyPet.getPet();
        if (!eventMyPet.isQuiet())
        {
            MPet.sendMessageToOwner(MyPetUtil.setColors(MyPetLanguage.getString("Msg_LvlUp")).replace("%petname%", MPet.Name).replace("%lvl%", "" + eventMyPet.getLevel()));
        }
        int lvl = eventMyPet.getLevel();
        MyPetSkillTree st = MPet.getSkillTree();
        if (st.hasLevel(lvl))
        {
            List<MyPetSkillTreeSkill> skillList = st.getLevel(lvl).getSkills();
            for (MyPetSkillTreeSkill skill : skillList)
            {
                if (MPet.getSkillSystem().hasSkill(skill.getName()))
                {
                    if (eventMyPet.isQuiet())
                    {
                        MPet.getSkillSystem().getSkill(skill.getName()).setLevel(MPet.getSkillSystem().getSkill(skill.getName()).getLevel() + 1);
                    }
                    else
                    {
                        MPet.getSkillSystem().getSkill(skill.getName()).upgrade();
                    }
                }
            }
        }

        MPet.setHealth(MPet.getMaxHealth());
    }
}