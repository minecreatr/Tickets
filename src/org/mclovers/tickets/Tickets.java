package org.mclovers.tickets;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Tickets extends JavaPlugin implements Listener {


    public void onEnable(){
        getLogger().info("Tickets has been enabled");
        getServer().getPluginManager().registerEvents(this, this);
        reloadTicketList();
    }

    public void onDisable(){
        getLogger().info("Tickets has been disabled");
        saveConfig();
        saveTicketList();
    }

    private FileConfiguration ticketList = null;
    private File ticketListFile = null;

    //reload ticketList
    public void reloadTicketList(){
        if(ticketListFile==null){
            ticketListFile = new File(getDataFolder(), "ticketList.yml");
        }
        ticketList = YamlConfiguration.loadConfiguration(ticketListFile);
    }

    //get ticketList
    public FileConfiguration getTicketList() {
        if (ticketListFile == null) {
            reloadTicketList();
        }
        return ticketList;
    }


   public void saveTicketList() {
        if (ticketList == null || ticketListFile == null) {
            return;
        }
        try {
            getTicketList().save(ticketListFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + ticketListFile, ex);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (this.getTicketList().getString(player.getName()+".response")!=null){
            player.sendMessage("§2Your ticket has a response, type /seeanswer to see the response");
        }
    }




    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("ticket")){
            if (!(args.length>=1)){
                return false;
            }
            String output = "";
            for (int i=0;i<args.length;i++){
                output = output + args[i]+ " ";
            }
            this.getTicketList().set(player.getName()+".message", "Player: "+player.getName()+" || Message: "+output);
            this.getTicketList().set(player.getName() + ".time", System.currentTimeMillis());
            player.sendMessage("§5Your Ticket has been submitted");
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("gettickets") && player.hasPermission("tickets.gettickets")){
            for (String name : this.getTicketList().getKeys(false)){
                long time = ((((System.currentTimeMillis() - this.getTicketList().getInt(name+".time"))/1000)/60)-23121240)-71583;
                player.sendMessage(this.getTicketList().getString(name+".message")+ " || Time: " + time+ " minute(s) ago");
            }
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("respond") && player.hasPermission("tickets.repond")){
            if (!(args.length>=2)){
                return false;
            }
            if (this.getTicketList().getString(args[0]+".message")==null){
                player.sendMessage("This player has not submitted a ticket");
                return true;
            }
            String output = "";
            for (int i=1;i<args.length;i++){
                output = output + args[i]+ " ";
            }

            this.getTicketList().set(args[0]+".response", output);
            this.getTicketList().set(args[0]+".message", null);
            this.getTicketList().set(args[0]+".time", null);
            player.sendMessage("Your response has been sent");
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("seeanswer")){
            if (this.getTicketList().getString(player.getName()+".response")==null){
                player.sendMessage("Your ticket does not have a reponse");
                return true;
            }
            player.sendMessage("Response: "+this.getTicketList().getString(player.getName()+".response"));
            this.getTicketList().set(player.getName()+".response", null);
            return true;
        }
        return false;
    }
}


