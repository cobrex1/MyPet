/*
 * This file is part of MyPet
 *
 * Copyright (C) 2011-2013 Keyle
 * MyPet is licensed under the GNU Lesser General Public License.
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.util.itemstringinterpreter;

import de.Keyle.MyPet.skill.skills.implementation.inventory.ItemStackComparator;
import de.Keyle.MyPet.util.Util;
import net.minecraft.server.v1_6_R2.NBTBase;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ConfigItem
{
    public enum DurabilityMode
    {
        Smaller, Bigger, NotUsed, Equal
    }

    ItemStack item;
    DurabilityMode durabilityMode = DurabilityMode.NotUsed;

    public ConfigItem(ItemStack item, DurabilityMode durabilityMode)
    {
        this.item = item;
        this.durabilityMode = durabilityMode;
    }

    public boolean compare(ItemStack compareItem)
    {
        if (item == null || item.getTypeId() == 0)
        {
            if (compareItem == null || compareItem.getTypeId() == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        if (compareItem == null)
        {
            return false;
        }
        if (item.getTypeId() != compareItem.getTypeId())
        {
            return false;
        }
        switch (durabilityMode)
        {
            case Bigger:
                if (compareItem.getDurability() <= item.getDurability())
                {
                    return false;
                }
                break;
            case Smaller:
                if (compareItem.getDurability() >= item.getDurability())
                {
                    return false;
                }
                break;
            case Equal:
                if (compareItem.getDurability() != item.getDurability())
                {
                    return false;
                }
                break;
        }
        if (item.hasItemMeta())
        {
            if (!ItemStackComparator.compareTagData(item, compareItem))
            {
                return false;
            }
        }
        return true;
    }

    public boolean compare(net.minecraft.server.v1_6_R2.ItemStack compareItem)
    {
        if (item.getTypeId() != compareItem.id)
        {
            return false;
        }
        switch (durabilityMode)
        {
            case Bigger:
                if (compareItem.getData() <= item.getDurability())
                {
                    return false;
                }
                break;
            case Smaller:
                if (compareItem.getData() >= item.getDurability())
                {
                    return false;
                }
                break;
            case Equal:
                if (compareItem.getData() != item.getDurability())
                {
                    return false;
                }
                break;
        }
        if (item.hasItemMeta())
        {
            return CraftItemStack.asNMSCopy(item).tag.equals(compareItem.tag);
        }
        return true;
    }

    public ItemStack getItem()
    {
        return item;
    }

    public DurabilityMode getDurabilityMode()
    {
        return durabilityMode;
    }

    public String toString()
    {
        return "ConfigItem{mode: " + durabilityMode.name() + ", item: " + item.toString() + "}";
    }

    public static ConfigItem createConfigItem(String data)
    {
        NBTBase nbtBase = null;
        if (data.contains("{"))
        {
            String tagString = data.substring(data.indexOf("{"));
            data = data.substring(0, data.indexOf("{"));
            try
            {
                nbtBase = ItemStringInterpreter.convertString(tagString);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        String[] splitData = data.split("\\s");

        int itemId = 1;
        int itemDamage = -1;
        DurabilityMode mode = DurabilityMode.NotUsed;

        if (splitData.length == 0)
        {
            return new ConfigItem(null, mode);
        }
        if (splitData.length >= 1)
        {
            if (Util.isInt(splitData[0]))
            {
                itemId = Integer.parseInt(splitData[0]);
            }
        }
        if (itemId != 0)
        {
            if (splitData.length >= 2)
            {
                if (splitData[1].startsWith("<"))
                {
                    mode = DurabilityMode.Smaller;
                    splitData[1] = splitData[1].substring(1);
                }
                else if (splitData[1].startsWith(">"))
                {
                    mode = DurabilityMode.Bigger;
                    splitData[1] = splitData[1].substring(1);
                }
                else
                {
                    mode = DurabilityMode.Equal;
                }
                if (Util.isInt(splitData[1]))
                {
                    itemDamage = Integer.parseInt(splitData[1]);
                }
            }

            net.minecraft.server.v1_6_R2.ItemStack is = new net.minecraft.server.v1_6_R2.ItemStack(itemId, 1, itemDamage);
            if (nbtBase != null && nbtBase instanceof NBTTagCompound)
            {
                is.setTag((NBTTagCompound) nbtBase);
            }

            return new ConfigItem(CraftItemStack.asBukkitCopy(is), mode);
        }
        return new ConfigItem(null, mode);
    }
}