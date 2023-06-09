package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
//        Train train=new Train();
//        List<Station>stationList=trainEntryDto.getStationRoute();
//
//        //M  converting list of station to Single route
//        StringBuilder sb=new StringBuilder();
//        for(Station station : stationList){
//            String temp=String.valueOf(station);
//            sb.append(temp).append(",");
//        }
//
//        String route =sb.toString();
//      //  System.out.println(route);
//
//        train.setDepartureTime(trainEntryDto.getDepartureTime());
//        train.setNoOfSeats(trainEntryDto.getNoOfSeats());
//        train.setBookedTickets(new ArrayList<>());
//        train.setRoute(route);
//        trainRepository.save(train);
//
//        return trainRepository.save(train).getTrainId();
        Train train = new Train();
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());

        List<Station> list = trainEntryDto.getStationRoute();
        String route = "";

        for(int i=0;i<list.size();i++){
            if(i==list.size()-1)
                route += list.get(i);
            else
                route += list.get(i) + ",";
        }
        train.setRoute(route);

        train.setDepartureTime(trainEntryDto.getDepartureTime());
        return trainRepository.save(train).getTrainId();

       // return null;
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
                                     //      a to b and c to d
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.

//        AtomicInteger idx= new AtomicInteger(1);
//        HashMap<String,Integer> hm=new HashMap<>();
//        EnumSet.allOf(Station.class)
//                .forEach(station -> hm.put(String.valueOf(station), idx.getAndIncrement()));

        // Convert enum to set and apply forEach()
//        Arrays.asList(Station.values())
//                .forEach(season -> System.out.println(season));
//        int cnt=0;
//        int fromId=hm.get(seatAvailabilityEntryDto.getFromStation());
//        int toId=hm.get(seatAvailabilityEntryDto.getToStation());
//
//        Train train=trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();
//        List<Ticket>ticketList=train.getBookedTickets();
//
//        for(Ticket ticket : ticketList){
//            int curr_fromId=hm.get(ticket.getFromStation());
//            int curr_toId=hm.get(ticket.getToStation());
//
//            if(curr_fromId<=fromId && curr_toId>=toId){
//                List<Passenger>list=ticket.getPassengersList();
//                for(int i=0;i<list.size();i++){
//                    cnt++;
//                }
//            }
//        }
//        int available=train.getNoOfSeats()-cnt;
//
//
////        Station.
//
//       return available;
        Train train=trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();
        List<Ticket>ticketList=train.getBookedTickets();
        String []trainRoot=train.getRoute().split(",");
        HashMap<String,Integer> map=new HashMap<>();
        for(int i=0;i<trainRoot.length;i++){
            map.put(trainRoot[i],i);
        }
        if(!map.containsKey(seatAvailabilityEntryDto.getFromStation().toString())||!map.containsKey(seatAvailabilityEntryDto.getToStation().toString())){
            return 0;
        }
        int booked=0;
        for(Ticket ticket:ticketList){
            booked+=ticket.getPassengersList().size();
        }
        int count=train.getNoOfSeats()-booked;
        for(Ticket t:ticketList){
            String fromStation=t.getFromStation().toString();
            String toStation=t.getToStation().toString();
            if(map.get(seatAvailabilityEntryDto.getToStation().toString())<=map.get(fromStation)){
                count++;
            }
            else if (map.get(seatAvailabilityEntryDto.getFromStation().toString())>=map.get(toStation)){
                count++;
            }
        }
        return count+2;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.
        Train train=trainRepository.findById(trainId).get();
        if(train==null) return null;
        String stations=train.getRoute();
        if(stations.contains(station.toString())==false)
            throw new Exception("Train is not passing from this station");
//        String routes[]=stations.split(",");

//        String Station="";
//        for( String route : routes){
//            if(String.valueOf(station)==route){
//
//                Station=route;
//            }
//        }

//        if(Station==""){
//            throw new Exception("Train is not passing from this station");
//        }
        int cnt=0;
        List<Ticket>list=train.getBookedTickets();
        for(Ticket ticket : list){
            if(ticket.getFromStation()==station){
                List<Passenger>list1=ticket.getPassengersList();
                for(int i=0;i<list1.size();i++){
                    cnt++;
                }
            }
        }

        return cnt;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        Train train=trainRepository.findById(trainId).get();
        List<Ticket>ticketList=train.getBookedTickets();
        int max=Integer.MIN_VALUE;
        boolean flag=false;
        for(Ticket ticket : ticketList){
            List<Passenger>passengerList =ticket.getPassengersList();
            for(Passenger passenger : passengerList){
                flag=true;
                if(passenger.getAge()>max){
                    max=passenger.getAge();
                }
            }
        }

        return flag==false ? 0 : max;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.
//        List<Train>trains=trainRepository.findAll();
//        List<Train>list=new ArrayList<>();
//        int cnt=0;
//        for(Train train : trains){
//            String route =train.getRoute();
//            String sta=String.valueOf(station);
//            if(route.contains(sta)){
//                list=trainRepository.findByDepartureTimeBetween(startTime,endTime);
//            }
//        }
//        List<Integer>res=new ArrayList<>();
//        for(Train train : list){
//            res.add(train.getTrainId());
//        }
//
//        return res;
        List<Integer> TrainList = new ArrayList<>();
        List<Train> trains = trainRepository.findAll();
        for(Train t:trains){
            String s = t.getRoute();
            String[] ans = s.split(",");
            for(int i=0;i<ans.length;i++){
                if(Objects.equals(ans[i], String.valueOf(station))){
                    int startTimeInMin = (startTime.getHour() * 60) + startTime.getMinute();
                    int lastTimeInMin = (endTime.getHour() * 60) + endTime.getMinute();


                    int departureTimeInMin = (t.getDepartureTime().getHour() * 60) + t.getDepartureTime().getMinute();
                    int reachingTimeInMin  = departureTimeInMin + (i * 60);
                    if(reachingTimeInMin>=startTimeInMin && reachingTimeInMin<=lastTimeInMin)
                        TrainList.add(t.getTrainId());
                }
            }
        }
        return TrainList;
    }

}
