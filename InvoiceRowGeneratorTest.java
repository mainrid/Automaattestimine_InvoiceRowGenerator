package invoice;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.text.*;
import java.util.Date;

import org.hamcrest.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceRowGeneratorTest {

	@Mock
	InvoiceRowDao dao;

	@InjectMocks
	InoviceRowGenerator generator;

	@Test
	public void dividesEquallyWhenNoRemainder() {

		generator.generateRowsFor(new BigDecimal(9), asDate("2012-02-15"), asDate("2012-04-02"));

		verify(dao, times(3)).save(argThat(getMatcherForAmount(3)));

		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-02-15"))));
		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-03-01"))));
		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-04-01"))));
	}

	@Test
	public void notEquallyDivisibleAmountDividedCorrectly() {

		generator.generateRowsFor(new BigDecimal(11), asDate("2012-03-08"), asDate("2012-05-02"));

		verify(dao, times(1)).save(argThat(getMatcherForAmount(3)));
		verify(dao, times(2)).save(argThat(getMatcherForAmount(4)));

		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-03-08"))));
		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-04-01"))));
		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-05-01"))));
	}
	
	@Test
	public void NextAmountThatIsLessThanMinimumShouldBeSummedUpWithCurrent() {

		generator.generateRowsFor(new BigDecimal(7), asDate("2012-03-08"), asDate("2012-05-02"));

		verify(dao, times(1)).save(argThat(getMatcherForAmount(3)));
		verify(dao, times(1)).save(argThat(getMatcherForAmount(4)));

		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-03-08"))));
		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-04-01"))));
	}
	
	@Test
	public void InitialAmountLessThanMinimumShouldNotBeDivided() {

		generator.generateRowsFor(new BigDecimal(2), asDate("2012-03-08"), asDate("2012-05-02"));

		verify(dao, times(1)).save(argThat(getMatcherForAmount(2)));
		
		verify(dao, times(1)).save(argThat(getMatcherForDate(asDate("2012-03-08"))));
	}
	
	

	@Test(expected = IllegalArgumentException.class)
	public void endDateShouldBeAfterBeginningDate() throws IllegalArgumentException {
		generator.getPaymentDates(asDate("2015-05-01"), asDate("2015-04-01"));
	}

	@Test
	public void IfZeroAmountThenNoInvoicesGenerated() {
		generator.generateRowsFor(new BigDecimal(0), asDate("2012-01-01"), asDate("2012-04-01"));
		verify(dao, never()).save(any(InvoiceRow.class));
	}
		
	private Matcher<InvoiceRow> getMatcherForAmount(final Integer amount) {

		// Matcher-i näide. Sama põhimõttega tuleb teha teine
		// Kuupäeva jaoks

		return new TypeSafeMatcher<InvoiceRow>() {

			@Override
			protected boolean matchesSafely(InvoiceRow item) {
				return amount.equals(item.amount.intValue());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(String.valueOf(amount));
			}
		};
	}

	private Matcher<InvoiceRow> getMatcherForDate(final Date date) {

		return new TypeSafeMatcher<InvoiceRow>() {

			@Override
			protected boolean matchesSafely(InvoiceRow item) {
				return date.equals(item.getDate());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(String.valueOf(date));
			}
		};
	}

	public static Date asDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}