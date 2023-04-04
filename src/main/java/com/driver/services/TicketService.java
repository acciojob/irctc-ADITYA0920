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
//        if(train.getNoOfSeats()<ticketList.size() + bookTicketEntryDto.getNoOfSeats())
//        {
//            throw new Exception("Less tickets are available");
//        }
        Integer bookedTickets=0;
        for(Ticket ticket : ticketList){
            List<Passenger>list=ticket.getPassengersList();
            bookedTickets+=list.size();
        }


        if(train.getNoOfSeats()<bookedTickets + bookTicketEntryDto.getNoOfSeats())
        {
            throw new Exception("Less tickets are available");
        }
        List<Integer>ids=bookTicketEntryDto.getPassengerIds();

        List<Passenger>passengerList=new ArrayList<>();

        for(Integer id :ids){
            passengerList.add(passengerRepository.findById(id).get());
        }

        String route=train.getRoute();
        String routes[]=route.split(",");

        int distance=0;

        String start=String.valueOf(bookTicketEntryDto.getFromStation());
        int start_index=-1;
        String end=String.valueOf(bookTicketEntryDto.getToStation());
        int end_index=-1;
        boolean flag=false;

        for(int i=0;i<routes.length;i++){
            String str=routes[i];
            if(str.equals(start)){
                flag=true;
                start_index=i;
                break;
            }
            if(str.equals(end)){
                flag=true;
                end_index=i;
                break;
            }
        }
//        if(flag=false){
//            throw  new Exception("Invalid stations");
//        }

        if((start_index==-1||end_index==-1)||end_index-start_index<0){
            throw new Exception("Invalid stations");
        }
        distance=end_index-start_index+1;
        int fair=bookTicketEntryDto.getNoOfSeats()*(distance)*300;

        Ticket ticket=new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTotalFare(fair);
        ticket.setPassengersList(passengerList);
        ticket.setTrain(train);


        train.getBookedTickets().add(ticket);
        //train.setNoOfSeats();
        passenger.getBookedTickets().add(ticket);
        train.setNoOfSeats(train.getNoOfSeats()-bookTicketEntryDto.getNoOfSeats());


       // passengerRepository.save(passenger);

        trainRepository.save(train);

        return ticketRepository.save(ticket).getTicketId();

    }
}
