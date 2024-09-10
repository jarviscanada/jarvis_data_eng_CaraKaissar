package ca.jrvs.apps.stockquote.dao;
import ca.jrvs.apps.stockquote.dao.models.Position;
import ca.jrvs.apps.stockquote.dao.services.PositionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PositionService_UnitTest {

    private PositionService positionService;

    @Mock
    private PositionDao mockDao;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        positionService = new PositionService(mockDao);
    }

    @Test
    public void testBuy_NewPosition() {
        String ticker = "AAPL";
        int numberOfShares = 100;
        double price = 150.00;

        when(mockDao.findById(ticker)).thenReturn(Optional.empty());

        Position result = positionService.buy(ticker, numberOfShares, price);

        assertNotNull(result);
        assertEquals(ticker, result.getTicker());
        assertEquals(numberOfShares, result.getNumOfShares());
        assertEquals(numberOfShares * price, result.getValuePaid(), 0.01);

        verify(mockDao).findById(ticker);
        verify(mockDao).save(any(Position.class));
    }

    @Test
    public void testBuy_ExistingPosition() {
        String ticker = "AAPL";
        int numberOfShares = 50;
        double price = 150.00;


        Position existingPosition = new Position();
        existingPosition.setTicker(ticker);
        existingPosition.setNumOfShares(100);
        existingPosition.setValuePaid(15000.00); // 100 shares at $150.00

        when(mockDao.findById(ticker)).thenReturn(Optional.of(existingPosition));

        Position result = positionService.buy(ticker, numberOfShares, price);

        assertNotNull(result);
        assertEquals(ticker, result.getTicker());
        assertEquals(150, result.getNumOfShares());
        assertEquals(15000.00 + (50 * 150.00), result.getValuePaid(), 0.01);

        verify(mockDao).findById(ticker);
        verify(mockDao).save(existingPosition);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuy_InvalidInput() {
        positionService.buy(null, 100, 150.00); // Invalid ticker
    }

    @Test
    public void testSell_ExistingPosition() {
        String ticker = "AAPL";

        when(mockDao.findById(ticker)).thenReturn(Optional.of(new Position()));

        positionService.sell(ticker);

        verify(mockDao).findById(ticker);
        verify(mockDao).deleteById(ticker);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSell_NonExistingPosition() {
        String ticker = "AAPL";

        when(mockDao.findById(ticker)).thenReturn(Optional.empty());

        positionService.sell(ticker); // Should throw exception
    }
}
