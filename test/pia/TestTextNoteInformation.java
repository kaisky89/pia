package pia;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: kaisky89
 * Date: 20.06.13
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public class TestTextNoteInformation {
    TextNoteInformation textNote1;
    TextNoteInformation textNote2;
    TextNoteInformation textNote3;
    TextNoteInformation textNote4;
    TextNoteInformation textNote5;

    @Before
    public void setUp() throws NotesCommunicatorException {
        textNote1 = new TextNoteInformation((long) 123, "Bar");
        textNote1.setId(1);
        textNote2 = new TextNoteInformation((long) 13, "Foo");
        textNote2.setId(2);
        textNote3 = new TextNoteInformation((long) 1873, "FooBar");
        textNote3.setId(3);

        textNote4 = new TextNoteInformation(textNote1.toXml());
        textNote4.setId(4);
        textNote5 = new TextNoteInformation(textNote4.toXml());
        textNote5.setId(5);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetText() throws Exception {
        Assert.assertEquals(textNote1.getText(), "Bar");
        Assert.assertEquals(textNote4.getText(), "Bar");
        Assert.assertEquals(textNote5.getText(), "Bar");
    }

    @Test
    public void testSetText() throws Exception {
        textNote1.setText("Foo");
        Assert.assertEquals(textNote1.getText(), "Foo");
    }
}
