package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db


        //getting passenger
        Passenger passenger=passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();

        //find train
        Train train=trainRepository.findById(bookTicketEntryDto.getTrainId()).get();


        //ticket corresponding to train
        List<Ticket>ticketList=train.getBookedTickets();
        if(train.getNoOfSeats()<ticketList.size() + bookTicketEntryDto.getNoOfSeats())
        {
            throw new Exception("Less tickets are available");
        }

        List<Integer>passengerList=bookTicketEntryDto.getPassengerIds();

        if(passengerList.contains(bookTicketEntryDto.getBookingPersonId())==false){
            throw new Exception("Invalid passenger id");
        }

        String route=train.getRoute();
        String routes[]=route.split(",");
        int distance=0;
        int i=0;;
        String start=String.valueOf(bookTicketEntryDto.getFromStation());
        int start_index=0;
        String end=String.valueOf(bookTicketEntryDto.getToStation());
        int end_index=0;
        boolean flag=false;
        for(i=0;i<routes.length;i++){
            String str=routes[i];
            if(str==start){
                flag=true;
                start_index=i;
            }
            if(str==end){
                flag=true;
                end_index=i;
            }
        }
        if(flag=false){
            throw  new Exception("Invalid stations");
        }
        distance=end_index-start_index+1;
        int fair=distance*100;

        Ticket ticket=new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTotalFare(fair);

        ticket.setTrain(train);
        ticket.getPassengersList().add(passenger);
        train.getBookedTickets().add(ticket);
        passenger.getBookedTickets().add(ticket);

        trainRepository.save(train);
       // passengerRepository.save(passenger);

       return ticket.getTicketId();

    }
}
