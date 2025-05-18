package com.yourtickets.ticketbookingsystem.service;

import com.yourtickets.ticketbookingsystem.model.Event;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class EventSorter {
    public enum SortCriteria {
        DATE
    }

    public static List<Event> mergeSort(List<Event> events, SortCriteria criteria) {
        if (events.size() <= 1) {
            return events;
        }

        int mid = events.size() / 2;
        List<Event> left = new ArrayList<>(events.subList(0, mid));
        List<Event> right = new ArrayList<>(events.subList(mid, events.size()));

        left = mergeSort(left, criteria);
        right = mergeSort(right, criteria);

        return merge(left, right, criteria);
    }

    private static List<Event> merge(List<Event> left, List<Event> right, SortCriteria criteria) {
        List<Event> result = new ArrayList<>();
        int i = 0, j = 0;

        Comparator<Event> comparator = getComparator(criteria);

        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        result.addAll(left.subList(i, left.size()));
        result.addAll(right.subList(j, right.size()));

        return result;
    }

    private static Comparator<Event> getComparator(SortCriteria criteria) {
        // Only DATE criteria is available now
        return Comparator.comparing(Event::getStartDateTime);
    }
}