/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.channel.handlers;

import client.Character;
import client.Client;
import constants.game.GameConstants;
import net.AbstractPacketHandler;
import net.packet.InPacket;
import server.life.Monster;
import server.life.MonsterInformationProvider;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.PacketCreator;

public class FieldDamageMobHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        int mobOid = p.readInt();    // packet structure found thanks to Darter (Rajan)
        int dmg = p.readInt();

        Character chr = c.getPlayer();
        MapleMap map = chr.getMap();

        if (map.getEnvironment().isEmpty()) {   // no environment objects activated to actually hit the mob
            FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to use an obstacle on mapid " + map.getId() + " to attack.");
            return;
        }

        Monster mob = map.getMonsterByOid(mobOid);
        if (mob != null) {
            if (dmg < 0 || dmg > GameConstants.MAX_FIELD_MOB_DAMAGE) {
                FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to use an obstacle on mapid " + map.getId() + " to attack " + MonsterInformationProvider.getInstance().getMobNameFromId(mob.getId()) + " with damage " + dmg);
                return;
            }

            map.broadcastMessage(chr, PacketCreator.damageMonster(mobOid, dmg), true);
            map.damageMonster(chr, mob, dmg);
        }
    }
}
