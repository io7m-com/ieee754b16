Originally posted to StackOverflow on 2011-05-28 by user "x4u":

You can Use Float.intBitsToFloat() and Float.floatToIntBits() to
convert them to and from primitive float values. If you can live with
truncated precision (as opposed to rounding) the conversion should
be possible to implement with just a few bit shifts.

I have now put a little more effort into it and it turned out not quite
as simple as I expected at the beginning. This version is now tested
and verified in every aspect I could imagine and I'm very confident
that it produces the exact results for all possible input values. It
supports exact rounding and subnormal conversion in either direction.

// ignores the higher 16 bits
public static float toFloat( int hbits )
{
    int mant = hbits & 0x03ff;            // 10 bits mantissa
    int exp =  hbits & 0x7c00;            // 5 bits exponent
    if( exp == 0x7c00 )                   // NaN/Inf
        exp = 0x3fc00;                    // -> NaN/Inf
    else if( exp != 0 )                   // normalized value
    {
        exp += 0x1c000;                   // exp - 15 + 127
        if( mant == 0 && exp > 0x1c400 )  // smooth transition
            return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16
                                            | exp << 13 | 0x3ff );
    }
    else if( mant != 0 )                  // && exp==0 -> subnormal
    {
        exp = 0x1c400;                    // make it normal
        do {
            mant <<= 1;                   // mantissa * 2
            exp -= 0x400;                 // decrease exp by 1
        } while( ( mant & 0x400 ) == 0 ); // while not normal
        mant &= 0x3ff;                    // discard subnormal bit
    }                                     // else +/-0 -> +/-0
    return Float.intBitsToFloat(          // combine all parts
        ( hbits & 0x8000 ) << 16          // sign  << ( 31 - 15 )
        | ( exp | mant ) << 13 );         // value << ( 23 - 10 )
}

// returns all higher 16 bits as 0 for all results
public static int fromFloat( float fval )
{
    int fbits = Float.floatToIntBits( fval );
    int sign = fbits >>> 16 & 0x8000;          // sign only
    int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

    if( val >= 0x47800000 )               // might be or become NaN/Inf
    {                                     // avoid Inf due to rounding
        if( ( fbits & 0x7fffffff ) >= 0x47800000 )
        {                                 // is or must become NaN/Inf
            if( val < 0x7f800000 )        // was value but too large
                return sign | 0x7c00;     // make it +/-Inf
            return sign | 0x7c00 |        // remains +/-Inf or NaN
                ( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
        }
        return sign | 0x7bff;             // unrounded not quite Inf
    }
    if( val >= 0x38800000 )               // remains normalized value
        return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
    if( val < 0x33000000 )                // too small for subnormal
        return sign;                      // becomes +/-0
    val = ( fbits & 0x7fffffff ) >>> 23;  // tmp exp for subnormal calc
    return sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
         + ( 0x800000 >>> val - 102 )     // round depending on cut off
      >>> 126 - val );   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
}

I implemented two small extensions compared to the book because the
general precision for 16 bit floats is rather low which could make
the inherent anomalies of floating point formats visually perceivable
compared to larger floating point types where they are usually not
noticed due to the ample precision.

The first one are these two lines in the toFloat() function:

if( mant == 0 && exp > 0x1c400 )  // smooth transition
    return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16 | exp << 13 | 0x3ff );

Floating point numbers in the normal range of the type size adopt the
exponent and thus the precision to the magnitude of the value. But
this is not a smooth adoption, it happens in steps: switching to the
next higher exponent results in half the precision. The precision now
remains the same for all values of the mantissa until the next jump
to the next higher exponent. The extension code above makes these
transitions smoother by returning a value that is in the geographical
center of the covered 32 bit float range for this particular half
float value. Every normal half float value maps to exactly 8192 32
bit float values. The returned value is supposed to be exactly in
the middle of these values. But at the transition of the half float
exponent the lower 4096 values have twice the precision as the upper
4096 values and thus cover a number space that is only half as large
as on the other side. All these 8192 32 bit float values map to the
same half float value, so converting a half float to 32 bit and back
results in the same half float value regardless of which of the 8192
intermediate 32 bit values was chosen. The extension now results in
something like a smoother half step by a factor of sqrt(2) at the
transition as shown at the right picture below while the left picture
is supposed to visualize the sharp step by a factor of two without
anti aliasing. You can safely remove these two lines from the code
to get the standard behavior.

covered number space on either side of the returned value:
       6.0E-8             #######                  ##########
       4.5E-8             |                       #
       3.0E-8     #########               ########

The second extension is in the fromFloat() function:

    {                                     // avoid Inf due to rounding
        if( ( fbits & 0x7fffffff ) >= 0x47800000 )
...
        return sign | 0x7bff;             // unrounded not quite Inf
    }

This extension slightly extends the number range of the half
float format by saving some 32 bit values form getting promoted to
Infinity. The affected values are those that would have been smaller
than Infinity without rounding and would become Infinity only due
to the rounding. You can safely remove the lines shown above if you
don't want this extension.

I tried to optimize the path for normal values in the fromFloat()
function as much as possible which made it a bit less readable due to
the use of precomputed and unshifted constants. I didn't put as much
effort into 'toFloat()' since it would not exceed the performance
of a lookup table anyway. So if speed really matters could use the
toFloat() function only to fill a static lookup table with 0x10000
elements and than use this table for the actual conversion. This is
about 3 times faster with a current x64 server VM and about 5 times
faster with the x86 client VM.

I put the code hereby into public domain.
