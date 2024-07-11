package com.celestino.planner.link;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.celestino.planner.trip.Trip;

@Service
public class LinkService
{
    @Autowired
    private LinkRepository linkRepository;

    public LinkResponse registerLink(LinkRequestPayload payload, Trip trip)
    {
        Link newLink = new Link(payload.title(), payload.url(), trip);

        linkRepository.save(newLink);

        return new LinkResponse(newLink.getId());
    }
}
